# [1.0.0-beta.16](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.15...v1.0.0-beta.16) (2021-11-20)


### Bug Fixes

* **bungee:** format message on sending server to allow resolving of placeholders ([49a9cbd](https://github.com/sVoxelDev/sChat/commit/49a9cbd188583eceba75cfc01ddc8efd05e4c6e8))
* **chat:** click to select (and delete) message not possible ([8f0c05a](https://github.com/sVoxelDev/sChat/commit/8f0c05ae46a8a489d7f56ea26b52e33488bc0220))


### Features

* **formats:** add option to center messages, e.g. broadcasts ([e313b2c](https://github.com/sVoxelDev/sChat/commit/e313b2c3d4225eb66a67466ec23c3b9e08d53720))

# [1.0.0-beta.15](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.14...v1.0.0-beta.15) (2021-11-20)


### Features

* **cmd:** add /broadcast command incl. config ([1309129](https://github.com/sVoxelDev/sChat/commit/13091295a5ddcd94958835a7205df878329baddb)), closes [#18](https://github.com/sVoxelDev/sChat/issues/18)

# [1.0.0-beta.14](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.13...v1.0.0-beta.14) (2021-11-20)


### Features

* **config:** add private chat config options ([98ed9f5](https://github.com/sVoxelDev/sChat/commit/98ed9f5331ac5d2a56a34b451032aaa3904d3764))

# [1.0.0-beta.13](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.12...v1.0.0-beta.13) (2021-11-19)


### Features

* add custom formatting for channels and players ([af2ac60](https://github.com/sVoxelDev/sChat/commit/af2ac60befa7d16eeb5539c4e511962629a38e92)), closes [#21](https://github.com/sVoxelDev/sChat/issues/21) [#5](https://github.com/sVoxelDev/sChat/issues/5)

# [1.0.0-beta.12](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.11...v1.0.0-beta.12) (2021-11-19)


### Bug Fixes

* **nicknames:** nickname not synchronized across servers ([22456f4](https://github.com/sVoxelDev/sChat/commit/22456f49c30a395b2b56810866142f24c20453ec)), closes [#14](https://github.com/sVoxelDev/sChat/issues/14)
* **packets:** bungee chat packets not handled properly ([fcb5bc1](https://github.com/sVoxelDev/sChat/commit/fcb5bc184b48facc9fcb0cc56a7e7df38f3ea814)), closes [#47](https://github.com/sVoxelDev/sChat/issues/47)

# [1.0.0-beta.11](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.10...v1.0.0-beta.11) (2021-11-18)


### Bug Fixes

* **cmd:** /nick commands not formatted correctly ([4c34576](https://github.com/sVoxelDev/sChat/commit/4c34576cf7619cf93a7145e6fd3729442a2b2c4a)), closes [#14](https://github.com/sVoxelDev/sChat/issues/14)
* **cmd:** /nick not working for other players ([8611edc](https://github.com/sVoxelDev/sChat/commit/8611edc167ae228dbb9da7a739d63d5e8f84cf73)), closes [#14](https://github.com/sVoxelDev/sChat/issues/14)
* **nicknames:** persist nicknames across restarts ([072857b](https://github.com/sVoxelDev/sChat/commit/072857b15ccee02db41477e14bbb66fa1bfd6280)), closes [#14](https://github.com/sVoxelDev/sChat/issues/14)

# [1.0.0-beta.10](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.9...v1.0.0-beta.10) (2021-11-18)


### Features

* **cmd:** add /nick command to change the display name of a player ([abd945b](https://github.com/sVoxelDev/sChat/commit/abd945b7d526700cf940edfa70adbb5dc475a23a)), closes [#14](https://github.com/sVoxelDev/sChat/issues/14) [#14](https://github.com/sVoxelDev/sChat/issues/14)
* **nicknames:** allow blocking names with regular expressions ([a552e58](https://github.com/sVoxelDev/sChat/commit/a552e585dcaeffafa0c3b722a8ea27a6528b1e4b)), closes [#14](https://github.com/sVoxelDev/sChat/issues/14)
* **nicknames:** allow bypassing blocked nicknames with `schat.nickname.set.blocked` ([d947d18](https://github.com/sVoxelDev/sChat/commit/d947d187ae72416f505877c57fe73df2dabe814f)), closes [#14](https://github.com/sVoxelDev/sChat/issues/14)

# [1.0.0-beta.9](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.8...v1.0.0-beta.9) (2021-11-17)


### Bug Fixes

* **chat:** console and command blocks cannot chat ([34d0691](https://github.com/sVoxelDev/sChat/commit/34d0691088ff258f442066588e657c77ce595dfd)), closes [#45](https://github.com/sVoxelDev/sChat/issues/45)
* last active chat not correctly loaded after restart ([06a4703](https://github.com/sVoxelDev/sChat/commit/06a47030ec48cd9da382283e33e4b58524629148))

# [1.0.0-beta.8](https://github.com/sVoxelDev/sChat/compare/v1.0.0-beta.7...v1.0.0-beta.8) (2021-11-17)


### Features

* **api:** add method to delete messages ([1b367e2](https://github.com/sVoxelDev/sChat/commit/1b367e262ad1b0e5e8ecf33ff3a58246bb159620))
* **chat:** add message deletion feature ([a7b20bc](https://github.com/sVoxelDev/sChat/commit/a7b20bc30c311eac27c66826f70867824848188a)), closes [#12](https://github.com/sVoxelDev/sChat/issues/12)

# [1.0.0-beta.7](https://github.com/Silthus/sChat/compare/v1.0.0-beta.6...v1.0.0-beta.7) (2021-11-16)


### Bug Fixes

* **cmd:** add missing permission checks to channel click leave/join events ([151a687](https://github.com/Silthus/sChat/commit/151a68701a1a40a7d06d0d61a306f7cae642db39)), closes [#41](https://github.com/Silthus/sChat/issues/41)


### Features

* **chat:** add an option to disable the channel footer ([6837db2](https://github.com/Silthus/sChat/commit/6837db20cbbb5871c9a6f808bf75c408979ad8b3))
* **chat:** clicking player names starts private conversation ([7b0ff8e](https://github.com/Silthus/sChat/commit/7b0ff8eddd35fa959707b2415688d3a338015c15)), closes [#39](https://github.com/Silthus/sChat/issues/39)

# [1.0.0-beta.6](https://github.com/Silthus/sChat/compare/v1.0.0-beta.5...v1.0.0-beta.6) (2021-11-16)


### Features

* persist active channel across restarts ([c8b4279](https://github.com/Silthus/sChat/commit/c8b427987c87888b0a46578ba9b9f8acc168a8b9)), closes [#15](https://github.com/Silthus/sChat/issues/15)
* remember active channel across restarts ([0092d34](https://github.com/Silthus/sChat/commit/0092d3430db3b97ae427d65c8c2973ad72acf658))

# [1.0.0-beta.5](https://github.com/Silthus/sChat/compare/v1.0.0-beta.4...v1.0.0-beta.5) (2021-11-15)


### Bug Fixes

* **cmd:** reload not removing console target from channel if `console: false` ([f77794f](https://github.com/Silthus/sChat/commit/f77794f24f17480a50d8fb901196dab0124c5672))


### Features

* **config:** add name config to console ([c30afb1](https://github.com/Silthus/sChat/commit/c30afb1cf17436a0bd8233b96f1c72dfd64d38fb))

# [1.0.0-beta.4](https://github.com/Silthus/sChat/compare/v1.0.0-beta.3...v1.0.0-beta.4) (2021-11-15)


### Bug Fixes

* **platform:** system messages display under the channel tabs ([cac4ffb](https://github.com/Silthus/sChat/commit/cac4ffbc62b48b981be3b68b79d595d93ecbd87b)), closes [#36](https://github.com/Silthus/sChat/issues/36)

# [1.0.0-beta.3](https://github.com/Silthus/sChat/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2021-11-15)


### Bug Fixes

* **platform:** drop paper support and shade adventure-text directly ([76c25cc](https://github.com/Silthus/sChat/commit/76c25cc76d29790d4b27f9976caf6ba3393e7f03)), closes [#34](https://github.com/Silthus/sChat/issues/34)

# [1.0.0-beta.2](https://github.com/Silthus/sChat/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2021-11-14)


### Features

* **cmd:** add non-disruptive `/schat reload` command ([486eb48](https://github.com/Silthus/sChat/commit/486eb48a7365e45ce0588aee598f6d6756422c79)), closes [#10](https://github.com/Silthus/sChat/issues/10)
* **integrations:** add optional PlaceholderAPI support ([4c5ed02](https://github.com/Silthus/sChat/commit/4c5ed02dd21e38c99313f0a739f57ef34c2ff0ff)), closes [#11](https://github.com/Silthus/sChat/issues/11)

# 1.0.0-beta.1 (2021-11-14)


### Bug Fixes

* **release:** lowercase artifactid and group ([688f377](https://github.com/Silthus/sChat/commit/688f3777abdbc0f7efe797d87dac96143d40088a))
* unsubscribe all from channel when removed ([f6e3ed1](https://github.com/Silthus/sChat/commit/f6e3ed15a6fc95195da5b8fdae0e41e98400b300))


### Features

* add icon to leave a conversation ([31db6dc](https://github.com/Silthus/sChat/commit/31db6dc47300f6061b25f87aeffec192a6fc68c7))
* initial beta release ([8cac128](https://github.com/Silthus/sChat/commit/8cac1281e9530898bcef3c799455f61d6942a91a))
* **view:** add unread message indicator to channels ([bf720d4](https://github.com/Silthus/sChat/commit/bf720d450184a7c6e51731fe2fbb6e31fba2adb4))
