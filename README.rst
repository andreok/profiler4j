This project is a fork of the official Profiler4j project by Antonio S. R. Gomes.
You can find it at sourceforge: http://profiler4j.sourceforge.net/

I've created this fork to add one or two features. Some have already been implemented,
see the changelog excerpt below.

Basically, what I want:
- Save/load functionality for snapshots.
- Exporting the call graph as a picture.
- Have each call graph dumped to disk automatically when taking a snapshot.
This helps when profiling many small pieces of a workflow.

Maybe I'll even implement some of those goals.

------------------------

FORK CHANGELOG (excerpt)

1.0-alpha3 (not officially released)
	- NEW: Export options in project settings get saved/loaded, too.
	- NEW: Snapshots can now be saved and loaded via the File menu.

1.0-alpha2
	- NEW: Automatically exported call graphs are always at maximum level of detail
	- NEW: Using SHIFT + left/right or home/end, the marking of nodes can be spread.
	- BUG: "Remote Command Monitor" hides on close and is disposed of, when done. (It used to stay open sometimes, hope this is fixed now.)

1.0-alpha1
	- NEW: Currently shown call graph may be saved as PNG image via export-menu
	- NEW: Project setting: After each snapshot, the callgraph is exported automatically.
	- NEW: The call graph may be copied to clip board.
