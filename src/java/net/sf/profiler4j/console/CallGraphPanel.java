/*
 * Copyright 2006 Antonio S. R. Gomes
 * Copyright 2009 Murat Knecht
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.profiler4j.console;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.JComboBox.KeySelectionManager;

import net.sf.profiler4j.console.client.Snapshot;
import net.sf.profiler4j.console.client.Snapshot.Method;
import net.sf.profiler4j.console.util.hover.HoverManager;
import net.sf.profiler4j.console.util.hover.HoverablePanel;

/**
 * CLASS_COMMENT
 * 
 * @author Antonio S. R. Gomes
 */
public class CallGraphPanel extends HoverablePanel {

    private static final int NCUT_INITIAL_VALUE = 40;
    private Snapshot snapshot;
    // [ix][iy]
    private MethodView[][] matrix = new MethodView[256][512];
    private List<MethodView> rootNodes;
    private MethodView selectedNode;
    private Set<MethodView> markedNodes = new HashSet<MethodView>();
    private Map<Method, MethodView> nodes = new HashMap<Method, MethodView>();
    static double maxTime;
    private int[] iyMax = new int[256];
    private int ixMax = 1;

    //
    // Visual attributes
    //
    private Stroke selStroke = new BasicStroke(3f);
    private Stroke linkStroke = new BasicStroke(.15f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_BEVEL);
    private Stroke selLinkStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_BEVEL);
    private Stroke defaultStroke = new BasicStroke(.25f);
    private Color disabledBgColor = Color.decode("#eeeedd");
    private Color disabledFgColor = Color.decode("#ccccbb");

    //
    // Geometry attributes
    //
    private boolean geometryOk;

    static int offsetX = 16;
    static int offsetY = 28;
    static int spacingX = 64;
    static int spacingY = 20;
    static int methodWidth = 150;
    static int methodHeight = 32;
    static int arcDims = 10;

    private List<LinkView> links = new ArrayList<LinkView>();
    private List<LinkView> selectedLinks = new ArrayList<LinkView>();

    private HoverManager hoverManager = new HoverManager();
    

    private Comparator<MethodView> byNetTimeComparator = new Comparator<MethodView>() {
        public int compare(MethodView o1, MethodView o2) {
            return (int) -Math.signum(o1.method.getNetTime() - o2.method.getNetTime());
        }
    };

    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            requestFocus();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            MethodView n = findNode(e.getX(), e.getY());
            if (n != selectedNode) {
                geometryOk = false;
                selectedNode = n;
                markedNodes.clear();
                if (null != n) {
                    markedNodes.add(n);
                }
                repaint();
            }
            requestFocus();
            System.out.println(e.getPoint());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hoverManager.reset();
        }
    };
    
    private KeyAdapter keyAdapter = new KeyAdapter() {

        @Override
        public void keyReleased(KeyEvent e) {
            
            // Mark more nodes starting from the already marked ones.
            if (e.isShiftDown()) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        markEnteringNodes();
                        geometryOk = false;
                        repaint();
                        break;
                    case KeyEvent.VK_RIGHT:
                        markExitingNodes();
                        geometryOk = false;
                        repaint();
                        break;
                    case KeyEvent.VK_HOME:
                        markEnteringTree();
                        geometryOk = false;
                        repaint();
                        break;
                    case KeyEvent.VK_END:
                        markExitingTree();
                        geometryOk = false;
                        repaint();
                        break;
                    default:
                        break;
                }
            }
        }
    };
    
    private boolean antialiased = true;

    // //////////////////////////////////////////////////////////////////////////
    // Constructor
    // //////////////////////////////////////////////////////////////////////////

    public CallGraphPanel() {
        setBackground(Color.decode("#ffffee"));
        Locale.setDefault(Locale.US);
        hoverManager.setContainer(this);
        hoverManager.registerHover(new MethodBoxHoverWindow());
        hoverManager.registerHover(new LinkHoverWindow());
        setFocusable(true);
        addKeyListener(keyAdapter);
        addMouseListener(mouseAdapter);
    }
    
    /**
     * Marks all nodes which are parents of currently marked nodes.
     */
    private void markEnteringNodes() {
        // This is complicated by the tree structure, where edges are directed. :/
        Set<MethodView> newNodes = new HashSet<MethodView>();
        for (MethodView n : nodes.values()) {
            for (MethodView child : n.children)
                if (n.visible && markedNodes.contains(child))
                    newNodes.add(n);
        }
        markedNodes.addAll(newNodes);
    }

    /**
     * Marks all nodes which are children of currently marked nodes.
     * <p>
     * In other words: Marks all children to which currently marked nodes have an exiting edge.
     */
    private void markExitingNodes() {
        Set<MethodView> newNodes = new HashSet<MethodView>();
        for (MethodView m : markedNodes) {
            for (MethodView child : m.children)
                if (child.visible)
                    newNodes.add(child);
        }
        markedNodes.addAll(newNodes);
    }

    /**
     * Marks all nodes that are direct or indirect parents of the currently marked nodes. 
     */
    private void markEnteringTree() {
        Set<MethodView> newNodes = new HashSet<MethodView>();
        do {
            newNodes.clear();
            for (MethodView n : nodes.values()) {
                for (MethodView child : n.children)
                    if (n.visible && markedNodes.contains(child))
                        newNodes.add(n);
            }
            // Fix-point calculation until no more parents are added.
        } while (markedNodes.addAll(newNodes));
    }

    /**
     * Marks all nodes that are direct or indirect children of the currently marked nodes. 
     */
    private void markExitingTree() {
        Set<MethodView> newNodes = new HashSet<MethodView>();
        do {
            newNodes.clear();
            for (MethodView m : markedNodes) {
                for (MethodView child : m.children)
                    if (child.visible)
                        newNodes.add(child);
            }
            // Fix-point calculation until no more children are added.
        } while (markedNodes.addAll(newNodes));
    }

    @Override
    public Object findHoverable(int x, int y) {
        MethodView n = findNode(x, y);
        if (n != null) {
            return n;
        }
        for (LinkView lnk : selectedLinks) {
            if (lnk.contains(x, y)) {
                return lnk;
            }
        }
        return null;
    }

    private MethodView findNode(int x, int y) {
        Rectangle rect = new Rectangle();
        for (MethodView node : nodes.values()) {
            if (node.visible) {
                rect.x = node.x;
                rect.y = node.y;
                rect.width = node.w;
                rect.height = node.h;
                if (rect.contains(x, y)) {
                    return node;
                }
            }
        }
        return null;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
        refresh();
        revalidate();
        repaint();
    }

    @Override
    public void paint(Graphics g_) {
        super.paint(g_);
        Graphics2D g = (Graphics2D) g_;
        g.setStroke(defaultStroke);
        if (antialiased) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        drawMethods(g);
    }

    // //////////////////////////////////////////////////////////////////////////
    // Helper methods
    // //////////////////////////////////////////////////////////////////////////

    private void drawMethods(Graphics2D g) {
        if (!geometryOk) {
            computeGeometry();
            geometryOk = true;
        }
        FontMetrics fm = getFontMetrics(getFont());
        //
        // Draw unselected links
        //
        g.setColor(Color.BLACK);
        g.setStroke(linkStroke);
        for (LinkView l : links) {
            l.paint(g);
        }
        //
        // Draw selected links
        //
        g.setColor(Color.BLUE);
        g.setStroke(selLinkStroke);
        g.setColor(Color.BLUE);
        g.setStroke(selLinkStroke);
        for (LinkView l : selectedLinks) {
            l.paint(g);
        }
        //
        // Draw unselected methods
        //
        g.setColor(Color.BLACK);
        g.setStroke(linkStroke);
        for (MethodView node : nodes.values()) {
            if (markedNodes.contains(node) || !node.visible) {
                continue;
            }
            g.setColor((node.visible) ? node.getColor() : disabledBgColor);
            g.fillRoundRect(node.x, node.y, node.w, node.h, arcDims, arcDims);
            g.setColor((node.visible) ? Color.BLACK : disabledFgColor);
            g.drawRoundRect(node.x, node.y, node.w, node.h, arcDims, arcDims);
            g.drawString(node.method.getMethodName(), node.x + 5, node.y + fm.getHeight());
            g.drawString(makeDetailText(node), node.x + 5, node.y + fm.getHeight() * 2);
        }

        for (MethodView node : markedNodes) {
            //
            // Draw selected method
            //
            g.setColor(node.getColor());
            g.setStroke(selStroke);
            g.fillRoundRect(node.x, node.y, node.w, node.h, arcDims, arcDims);
            g.setColor(Color.BLUE);
            g.drawRoundRect(node.x, node.y, node.w, node.h, arcDims, arcDims);
            g.setColor(Color.BLACK);
            g.drawString(node.method.getMethodName(), node.x + 5, node.y + fm.getHeight());
            g.drawString(makeDetailText(node), node.x + 5, node.y + fm.getHeight() * 2);
            g.setStroke(defaultStroke);
        }
    }

    /**
     * @param n
     * @return formatted detail
     */
    private String makeDetailText(MethodView n) {
        return String.format("N:%.1f AN:%.3f H:%d", n.method.getNetTime(), n.method
            .getNetTime()
                / n.method.getHits(), n.method.getHits());
    }

    private void computeGeometry() {
        //
        // Compute method boxes
        //
        for (MethodView node : nodes.values()) {
            node.x = node.ix * (methodWidth + spacingX) + offsetX;
            node.y = node.iy * (methodHeight + spacingY) + offsetY;
            node.w = methodWidth;
            node.h = methodHeight;
        }
        links.clear();
        selectedLinks.clear();
        //
        // Compute links segments
        //
        for (MethodView src : nodes.values()) {
            if (!src.visible) {
                continue;
            }
            for (MethodView dst : src.children) {
                if (!dst.visible) {
                    continue;
                }
                LinkView link = new LinkView(src, dst);
                if (markedNodes.contains(src) || markedNodes.contains(dst)) {
                    selectedLinks.add(link);
                } else {
                    links.add(link);
                }
            }
        }
    }

    private void refresh() {
        geometryOk = false;
        selectedNode = null;
        markedNodes.clear();
        createNodes();
        layNodes(rootNodes, 0, 0);
        applyNCut(NCUT_INITIAL_VALUE);
    }

    private void layNodes(List<MethodView> nodeList, int level, int y) {
        for (int i = 0; i < iyMax.length; i++) {
            iyMax[i] = 0;
        }
        ixMax = 1;
        Collections.sort(nodeList, byNetTimeComparator);

        // Position the root node along the left border
        // and position their children, subsequently processing
        // the entire graph.
        for (MethodView rootNode : nodeList) {
            // The root nodes form the first vertical layer.
            rootNode.ix = level;
            rootNode.iy = iyMax[0]++;
            rootNode.visible = true;
            
            // Update the matrix and process the root's children.
            matrix[rootNode.ix][rootNode.iy] = rootNode;
            lay(1, rootNode.children);
        }

        //
        // Sort columns
        //
        // For each x layer sort the nodes by the respective duration
        // and re-position them in the matrix that those with the highest
        // time come out on top.
        List<MethodView> colNodes = new ArrayList<MethodView>();
        for (int ix = 0; ix < ixMax; ix++) {
            colNodes.clear();
            for (int iy = 0; iy < iyMax[ix]; iy++) {
                colNodes.add(matrix[ix][iy]);
            }
            Collections.sort(colNodes, byNetTimeComparator);
            int iy = 0;
            for (MethodView n : colNodes) {
                matrix[ix][iy] = n;
                n.iy = iy;
                iy++;
            }
        }

        // Determine the maximum number of methods stacked on top of each other
        // i.e. on the y-axis.
        int ymax_ = -1;
        for (int iy : iyMax) {
            ymax_ = Math.max(iy, ymax_);
        }
        
        setPreferredSize(new Dimension(
                ixMax * (methodWidth + spacingX) + offsetX,
                (ymax_ * (methodHeight + spacingY)) + offsetY));
        repaint();
        revalidate();
    }

    @SuppressWarnings("unused")
    private void swapY(int x, int y1, int y2) {
        MethodView aux = matrix[x][y1];
        matrix[x][y1] = matrix[x][y2];
        matrix[x][y2] = aux;
        int yAux = matrix[x][y1].iy;
        matrix[x][y1].iy = matrix[x][y2].iy;
        matrix[x][y2].iy = yAux;
    }

    @SuppressWarnings("unused")
    private void dumpNode(MethodView rootNode) {
        System.out.format("%23s: ix=%2d iy=%2d\n",
                          rootNode.method.getMethodName(),
                          rootNode.ix,
                          rootNode.iy);
    }

    /**
     * Determine the x,y coordinates (in node coordinates) for all
     * given nodes.
     * <p>
     * Only invisible nodes are considered, i.e. nodes that have not
     * yet been layed out.
     * <p>
     * The highest x coordinate is updated. 
     * 
     * @param ix
     * @param nodes
     */
    private void lay(int ix, List<MethodView> nodes) {
        for (MethodView node : nodes) {
            if (node.visible) {
                continue;
            }
            node.ix = ix;
            node.iy = iyMax[ix]++;
            node.visible = true;
            // dumpNode(n);
            matrix[node.ix][node.iy] = node;
            lay(ix + 1, node.children);
        }
        ixMax = Math.max(ixMax, ix);
    }
    
    public void applyNCut(int n) {
        List<MethodView> aux = new ArrayList<MethodView>(nodes.values());
        Collections.sort(aux, byNetTimeComparator);
        int ncut = n;
        for (MethodView node : aux) {
            ncut--;
            node.visible = (ncut >= 0);
            // dumpNode(node);
        }
        
        // Remove the selection from a node, if it's not visible.
        if (selectedNode != null && !selectedNode.visible) {
            selectedNode = null;
        }
        // Unmark all nodes that are not visible.
        for (MethodView m : new ArrayList<MethodView>(markedNodes)) {
            if (!m.visible) {
                markedNodes.remove(m);
            }
        }
        geometryOk = false;
    }

    private void createNodes() {
        maxTime = -1;
        nodes.clear();
        if (snapshot == null) {
            return;
        }
        for (Method method : snapshot.getMethods().values()) {
            MethodView node = new MethodView();
            node.method = method;
            maxTime = Math.max(maxTime, method.getNetTime());
            nodes.put(method, node);
        }
        for (MethodView node : nodes.values()) {
            for (Method m : node.method.getChildrenTimes().keySet()) {
                node.children.add(nodes.get(m));
            }
        }
        findRootNodes();
    }

    private void findRootNodes() {
        for (MethodView node : nodes.values()) {
            for (Method childMethod : node.method.getChildrenTimes().keySet()) {
                if (childMethod != node.method) {
                    nodes.get(childMethod).root = false;
                }
            }
        }
        rootNodes = new ArrayList<MethodView>();
        for (MethodView node : nodes.values()) {
            if (node.root) {
                rootNodes.add(node);
                // System.out.println("root " + node.method.getName());
            }
        }
    }

}

class MethodView {

    Method method;
    List<MethodView> children = new ArrayList<MethodView>();

    int x;
    int y;
    int w;
    int h;
    Color c;

    int ix;
    int iy;

    boolean visible;
    boolean root = true;

    Color getColor() {
        if (c == null) {
            int k = (int) ((method.getNetTime() / CallGraphPanel.maxTime) * 192);
            k = (k < 0) ? 0 : ((k > 255) ? 255 : k);
            c = new Color(255, 255 - k, 255 - k);
        }
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        return method.getId() == ((MethodView) obj).method.getId();
    }

    @Override
    public int hashCode() {
        return method.getId();
    }
}

class LinkView {

    private static double tolerance = 4;

    protected Point2D.Double[] n;
    protected GeneralPath path = new GeneralPath();
    private MethodView src;
    private MethodView dst;

    public LinkView(MethodView src, MethodView dst) {
        this.src = src;
        this.dst = dst;
        updateShape();
    }

    public MethodView getDst() {
        return this.dst;
    }

    public MethodView getSrc() {
        return this.src;
    }

    private double k = 10;

    public void updateShape() {
        path.reset();

        if (src.x >= dst.x) {
            if (src.y == dst.y) {
                k = CallGraphPanel.spacingX * .3;
                n = new Point2D.Double[6];
                n[0] = new Point2D.Double(src.x + CallGraphPanel.methodWidth, src.y
                        + CallGraphPanel.methodHeight / 2);
                n[5] = new Point2D.Double(dst.x, dst.y + CallGraphPanel.methodHeight / 2);
                n[1] = new Point2D.Double(n[0].x + k, n[0].y);
                n[2] = new Point2D.Double(n[0].x + k, n[0].y - k * 2);
                n[3] = new Point2D.Double(n[5].x - k, n[5].y - k * 2);
                n[4] = new Point2D.Double(n[5].x - k, n[5].y);
            } else if (src.y < dst.y) {
                k = CallGraphPanel.spacingX * .3;
                n = new Point2D.Double[6];
                n[0] = new Point2D.Double(src.x + CallGraphPanel.methodWidth, src.y
                        + CallGraphPanel.methodHeight / 2);
                n[5] = new Point2D.Double(dst.x, dst.y + CallGraphPanel.methodHeight / 2);
                n[1] = new Point2D.Double(n[0].x + k, n[0].y);
                n[2] = new Point2D.Double(n[0].x + k, n[0].y + k * 2);
                n[3] = new Point2D.Double(n[5].x - k, n[5].y - k * 2);
                n[4] = new Point2D.Double(n[5].x - k, n[5].y);
            } else {
                k = CallGraphPanel.spacingX * .3;
                n = new Point2D.Double[6];
                n[0] = new Point2D.Double(src.x + CallGraphPanel.methodWidth, src.y
                        + CallGraphPanel.methodHeight / 2);
                n[5] = new Point2D.Double(dst.x, dst.y + CallGraphPanel.methodHeight / 2);
                n[1] = new Point2D.Double(n[0].x + k, n[0].y);
                n[2] = new Point2D.Double(n[0].x + k, n[0].y - k * 2);
                n[3] = new Point2D.Double(n[5].x - k, n[5].y + k * 2);
                n[4] = new Point2D.Double(n[5].x - k, n[5].y);
            }
        } else {
            n = new Point2D.Double[4];
            n[0] = new Point2D.Double(src.x + CallGraphPanel.methodWidth, src.y
                    + CallGraphPanel.methodHeight / 2);
            n[3] = new Point2D.Double(dst.x, dst.y + CallGraphPanel.methodHeight / 2);
            k = CallGraphPanel.spacingX * .3;
            n[1] = new Point2D.Double(n[0].x + k, n[0].y);
            n[2] = new Point2D.Double(n[3].x - k, n[3].y);
        }

        Point2D.Double cp1;
        Point2D.Double cp2;
        Point2D.Double p;
        path.moveTo((float) n[0].x, (float) n[0].y);
        int i = 1;
        for (i = 1; i < (n.length - 1); i++) {
            cp1 = n[i];
            cp2 = n[i];
            p = midPoint(n[i], n[i + 1]);
            path.curveTo((float) cp1.x,
                         (float) cp1.y,
                         (float) cp2.x,
                         (float) cp2.y,
                         (float) p.x,
                         (float) p.y);
        }
        path.lineTo((float) n[i].x, (float) n[i].y);
    }

    public boolean contains(int x, int y) {
        if (n != null) {
            Line2D seg = new Line2D.Double();
            for (int i = 0; i < (n.length - 1); i++) {
                seg.setLine(n[i].x, n[i].y, n[i + 1].x, n[i + 1].y);
                if (seg.ptSegDist(x, y) <= tolerance) {
                    return true;
                }

            }
        }
        return false;
    }

    public void paint(Graphics2D g) {
        g.draw(path);
    }

    private static Point2D.Double midPoint(Point2D.Double p1, Point2D.Double p2) {
        return new Point2D.Double((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
}
