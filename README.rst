This project is a fork of the official Profiler4j project by Antonio S. R. Gomes.
You can find it at sourceforge: http://profiler4j.sourceforge.net/

I've created this fork to add one or two features, such as:

- save/load functionality for snapshots
- exporting the call graph as a picture
- allowing to have each snapshot dumped to disk automatically, which helps when profiling many small pieces of a workflow
- ...

Maybe I'll even implement some of those goals.

------------------------

FORK CHANGELOG (excerpt)

1.0-alpha1
	- NEW: Currently shown call graph may be saved as PNG image via export-menu
	- NEW: Project setting: After each snapshot, the callgraph is exported automatically.
	- NEW: The call graph may be copied to clip board. 
	- NEW: Enabled PNG compression.
