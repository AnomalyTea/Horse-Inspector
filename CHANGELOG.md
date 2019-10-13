# Changelog
All notable changes to this project will be documented in this file.

## [1.3.0] - 2019-10-13
### Added
- Permission nodes for general use and config reloading.
- Option to match a specific item name.

## [1.2.1] - 2019-04-27
- Updated to Minecraft version 1.14

## [1.2.0] - 2019-02-22
### Added
- Config option to enable/disable showing of tamer.
- Config option to change the triggering item to something other than a stick.
- Config option to enable/disable update checking on load.
- `/horseinspector reload` command to reload config without restarting server.

### Changed
- Jump percent now calculated using meters, rather than internal jumpstrength value.

## [1.1.1] - 2019-02-12
### Fixed
- Mules are now properly recognized

## [1.1.0] - 2019-02-10
### Added
- Shows name of player who tamed the horse (thanks [datatags](https://github.com/datatags)).
- Now also works with Donkeys and Llamas.
- Checks for an updated version on spigotmc.org and outputs a console message if one exists.

## [1.0.0] - 2019-02-09
### Release
- Hitting a horse with a stick will do no damage and sends the player the horse's speed, hp, and jump height.
