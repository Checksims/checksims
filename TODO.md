Checksims To-Do
===============

Unit Tests:
- Additional CLI parsing tests
- Integration tests
- Output strategy tests
- Similarity Matrix tests
- Algorithm application tests

Code Cleanup
- Exception rewrite. More specific exceptions than just ChecksimsException
- Rewrite of Smith-Waterman, removing many otherwise-unused utility classes and adding tests
- Refactor a lot of the data structures used throughout --- List is very overused
- Refactor SimilarityMatrix (add clarity, handle potentially missing results, etc)

Minor Improvements
- Archive directory support
- Add handling for empty submissions (flag to include, default do not include)
- Token Annotations
- Leading & Trailing Whitespace Trimming Preprocessor
- Support for stateful comparison algorithms (Registries for methods returning an instance)

Major new features
- Comment stripping preprocessors
- Greedy String Tiling algorithm

Major Reworks
- Framework for non-pairwise comparisons (possible with shared state)
- Framework for new types of Tokens (AST-based, character-backed, etc)
