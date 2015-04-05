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
- Remove FileReader, replace with Apache Commons IO FileUtils.readFileToString()
- Remove UnorderedPair, replace with Apache Commons Pair. Add PairGenerationStrategy interface.
- Rewrite of Smith-Waterman, removing many otherwise-unused utility classes and adding tests

Minor Improvements
- Archive directory support

Major new features
- Comment stripping preprocessors
- Greedy String Tiling algorithm

Major Reworks
- Framework for non-pairwise comparisons (possible with shared state)
- Framework for new types of Tokens (AST-based, character-backed, etc)

