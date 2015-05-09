Checksims To-Do
===============

Unit Tests
----------
- Additional CLI parsing tests
- Integration tests
- Smith-Waterman Algorithm has 2 untested methods

Project Administration
----------------------
- Clean up README
- Generate User's Guide in usable web format - Markdown or HTML

Code Cleanup
------------
- Exception rewrite. Convert many RuntimeExceptions to checked exceptions
- Add ability to blacklist certain implementations in a registry
- Change Common Code Removal to a preprocessor

Minor Improvements
------------------
- Archive directory support
- Add handling for empty submissions (flag to include, default do not include)
- Leading and Trailing Whitespace Trimming Preprocessor
- Further optimization of Smith-Waterman Algorithm

Major new features
------------------
- Comment stripping preprocessors
- Greedy String Tiling algorithm
- Token Annotations
- REST API and web interface

Major Reworks
-------------
- Framework for non-pairwise comparisons (possible with shared state)
- Framework for new types of Tokens (AST-based, character-backed, etc)
