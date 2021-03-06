# Matag

Early Access version of an MTG-like game implementation.
Please note that we are not affiliated in any way with the MTG creators, nor we claim any copyright over their game or art assets.

Try it out: https://matag-admin.herokuapp.com/

In case you are the only one online, you can play against yourself by opening two browser sessions (windows/tabs) with the address above.

The name "Matag" comes from the combination of the initials of both its inspiration ("Magic: The Gathering") and its initial and main developer (Antonio Alonzi).

For any questions or comments, please contact antonioalonzi85@gmail.com.

![Snapshot](README_SNAPSHOT.png)


## Try it now!

https://matag-game.herokuapp.com/ui/game

In case you are the only one online, you can play against yourself by opening two browser sessions (windows/tabs) with the address above.


## Building

The application is divided in modules:
- [admin](admin/README.md): responsible for user and decks management (not implemented yet).
- [cards](cards/README.md): shared library for cards.
- [game](game/README.md): responsible for playing matches.
- utils: general utility functions.


## Contributing

### Backlog

The backlog is managed in Kanban style at:
 - https://github.com/antonioalonzi/matag/projects/1

Stories labelled `with guidelines` are very well described and easy to be picked up for people who want to start
contributing and learning the project.


### CI/CD


#### Continuous Integration on Travis

[![Build Status](https://travis-ci.com/antonioalonzi/matag.svg?branch=master)](https://travis-ci.com/antonioalonzi/matag)

 - Travis: https://travis-ci.com/antonioalonzi/matag

Tests run for all the modules together.
For the `game` module `Regression` tests are skipped. Read [game README](game/README.md) for more info.


#### Continuous Deployment on Heroku

Applications are continuously deployed on heroku:
 - Admin: https://dashboard.heroku.com/apps/matag-admin
 - Game: https://dashboard.heroku.com/apps/matag-game


## License

Copyright © 2019, 2020 Antonio Alonzi and [contributors](https://github.com/antonioalonzi/matag/graphs/contributors)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
