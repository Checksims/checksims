Changelog
=========

v1.2.0
-----------------------
- Feature: Archive Directory support
- Enhancement: New, faster version of Smith-Waterman algorithm
- Enhancement: Major performance improvements

v1.1.1
------
- Feature: Add -version flag to view current version
- Feature: Add Whitespace Deduplication preprocessor
- Bugfix: common code removal now functional
- Bugfix: Fix submission generation empty files and submissions
- Bugfix: Use sane, deterministic defaults for commandline options
- Enhancement: Make preprocessing parallel
- Enhancement: Make common code removal parallel
- Enhancement: Improve multithreading, increasing performance
- Other: Remove LCS algorithm due to poor performance

v1.1.0
------
- Enhancement: Major internal improvements to token handling

v1.0.2
------
- Feature: Add -r flag for recursive submission building
- Feature: Added -vv flag (very verbose logging)
- Feature: Added ability to specify multiple output formats for a single run
- Enhancement: Improved logging
- Enhancement: Early exit with warning if no submissions detected
- Other: Submissions are no longer built recursively by default

v1.0.1
------
- Enhancement: Major changes to internal handling of tokens
- Enhancement: Significant performance improvements
- Bugfix: Several small bugfixes to Common Code Removal

v1.0.0
------
- Initial Release
