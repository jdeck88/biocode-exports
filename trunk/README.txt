This set of code is purely meant for exporting Moorea Biocode Project data to different formats.
The goal is to export data in the following formats

ISATab
BOLD
Merritt

Since we expect the process of data publishing for the Moorea Biocode Project to be involved and ongoing,
this code-base will act as the source for all export data.   Most of this work is expected to occur in 2014.

To run this codebase, do the following:

1. svn checkout http://biocode-exports.googlecode.com/svn/trunk/ biocode-exports-read-only

2. rename biocode-exports.template.props to biocode-exports.props (and change property values accordingly)

3. Run the main methods in various classes in the exports package (e.g. exports/isaTab.java)