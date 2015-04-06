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
- Remove UnorderedPair, replace with Apache Commons Pair. Add PairGenerationStrategy interface.
- Rewrite of Smith-Waterman, removing many otherwise-unused utility classes and adding tests
- Refactor a lot of the data structures used throughout --- List is very overused

Minor Improvements
- Archive directory support
- Add handling for empty submissions (flag to include, default do not include)

Major new features
- Comment stripping preprocessors
- Greedy String Tiling algorithm

Major Reworks
- Framework for non-pairwise comparisons (possible with shared state)
- Framework for new types of Tokens (AST-based, character-backed, etc)
