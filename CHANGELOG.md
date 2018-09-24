# Changelog
All notable changes to this project will be documented in this file.

## [1.1] - 23-08-2018
### Added
- Changed "config.xml" to "jmarkpad.properties". (JMarkPad no longer user XML files)
- JMarkPad will now remember the last directory you worked in and open it by default the next time you are searching for your documents.
- Unique changelog file

### Fixed
- Fixed a bug that caused opening an existing document to automatically mark it as already edited.

### Changed
- Various GUI improvements ("About" section reworked, color sistem rewritten, button appearance improved).
- Updated JFoenix from 8.0.1 to 8.0.4.
- Updated Flexmark from 0.32.4 to 0.34.0.

### Removed
- releaseNotes folder

## [1.0] - 28-02-2018
### Added
- Various theme improvements and optimizations.
- Tab close button.
- Added markdown examples.
- Added extra information about the project ("About" section).
- Adjusted window border size to make resizing the window easier.
- Replaced *txtmark* with *flexmark* (Mainly because flexmark is still being developed and supported).

### Fixed
- Fixed a bug that showed an incorrect file name in the save confirmation alert.
- Improved confirmation alert look and feel.
- Catch the exception and properly print when the user settings file (config.xml) is missing and is created.

## [0.1] - 29-01-2018
### Added
- "Save as" option added.
- "Save all" option added.
- Theme (Color) edit option.
- JMarkPad now remembers where each file is saved, it doesn't ask for location each time you save.
- Load a file directly to a new tab when using (Open with-> JMarkPad).
- User settings saved on close and opened on start.

### Fixed
- Fixed a bug that allowed the user to open a file more than once.
- Each new tab now has a unique name.
- Fixed a bug that would open a new file when opening an existing file.

## [0.0.1] - 17-01-2018
### Added
* "Open", "Save", "Close" and "New" file options.
* Multiple open files at the same time (Diferent tabs).
* Realtime preview of how the markdown text would look like on web.