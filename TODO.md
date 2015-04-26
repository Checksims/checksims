Checksims To-Do
===============

Unit Tests
----------
- Additional CLI parsing tests
- Integration tests

Project Administration
----------------------
- Clean up README
- Generate User's Guide in usable web format - Markdown or HTML

Code Cleanup
------------
- Exception rewrite. Convert many RuntimeExceptions to checked exceptions
- Clean up AlgorithmResults (still very messy, poorly documented)

Minor Improvements
------------------
- Archive directory support
- Add handling for empty submissions (flag to include, default do not include)
- Leading and Trailing Whitespace Trimming Preprocessor
- Optimizations, bugfixes, and further testing of Smith-Waterman algorithm implementation

Major new features
------------------
- Comment stripping preprocessors
- Greedy String Tiling algorithm
- Token Annotations

Major Reworks
-------------
- Framework for non-pairwise comparisons (possible with shared state)
- Framework for new types of Tokens (AST-based, character-backed, etc)
