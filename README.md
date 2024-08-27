<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.20-7F52FF?logo=kotlin" />
  <img src="https://img.shields.io/badge/Gradle-8.7-02303A?logo=gradle" />
  <img src="https://img.shields.io/badge/Android-26+-34A853?logo=android" />
  <img src="https://img.shields.io/badge/Compose-1.6.7-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/github/license/LiveFastEatTrashRaccoon/RaccoonForFriendica" />
</div>

# RaccoonForFriendica

This is a client for the Friendica federated social platform powered by Kotlin Multiplatform (KMP)
and Compose Multiplatform (CMP). The reference platform is currently Android.

The project is _heavily_ inspired by the **RaccoonForLemmy** app for Lemmy, which demonstrated
something like this can be achieved with this tech stack.
This is why it retains part of its name, making clear that the two projects are related and share a
lot of common heritage.

## Want to try it?

Here are some options to install the application on your device. The best way to install testing APKs
is Obtainium, please insert this repository's URL as a source.

```
https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica
```

<div align="center">
  <div style="display: flex; flex-flow: row wrap; justify-content: center; align-items: center;">
    <a href="https://github.com/ImranR98/Obtainium/releases"><img width="200" src="https://github.com/user-attachments/assets/1501ad39-c581-4760-96ce-6d5181ab6207" /></a>
  </div>
</div>

> [!TIP]
> Make sure to check the "Include pre-releases" option (in order to receive all alpha and beta
> builds):

## Screenshots

<div align="center">

  <img width="310" src="https://github.com/user-attachments/assets/42bf2862-6afa-4f26-9061-d8294b28221b" alt="timeline screen" />
  <img width="310" src="https://github.com/user-attachments/assets/124ce939-a86c-441f-a959-97b98b3819d5" alt="user detail screen" />
  <img width="310" src="https://github.com/user-attachments/assets/b582f0ec-a4fc-4e3b-9bd0-fea705fa3729" alt="post detail screen" />
  <img width="310" src="https://github.com/user-attachments/assets/08804e47-4143-4f2b-ade1-baa1f7839c17" alt="settings screen" />
  <img width="310" src="https://github.com/user-attachments/assets/3b9473e5-75ce-4569-9d20-c2e4a4f9bed9" alt="explore screen (hashtags tab)" />
  <img width="310" src="https://github.com/user-attachments/assets/40b2a112-2aa4-49ab-b3b0-227a5bb680d3" alt="notifications screen" />
  <img width="310" src="https://github.com/user-attachments/assets/ca54f189-fcc0-4bd5-ab4f-79c796741ea8" alt="group opened in forum mode" />
  <img width="310" src="https://github.com/user-attachments/assets/08a9d437-d4af-48fa-b2de-efb56c21d0be" alt="thread screen within a group with replies" />
</div>

## Why was the project started and why is it called like that?

Because raccoons are so adorable, aren't they? 🦝🦝🦝

Joking apart, one of the main goals was to experiment with Kotlin multiplatform (KMP) and Compose
Multiplatform (CMP). This project has a fair degree of complexity and allows to put under stress
the existing solutions and evaluate what they behave like with common tasks (image loading, HTML
rendering, file system and gallery access, networking, local database management, shared
preferences, navigation, access to resources like fonts/drawables/localization, etc.).

Secondly, the Android ecosystem for Friendica was a little lacking, especially with few native
apps (except [Dica](https://github.com/jasoncheng/dica),
whereas [Friendiqa](https://git.friendi.ca/lubuwest/Friendiqa) is a very _cute_ example of Qt on
Android, although not a traditional mobile app at all). I ❤️ Kotlin, I ❤️ Free and Open Source
Software and I ❤️ native app development, so there was a niche that could be filled.

In the third place, we were wondering whether the adoption of a platform like Friendica could be
improved with a user-friendly and easy to use mobile app that abstracts away some of the
complications of the current web UI. This is why this app looks a lot like a plain Mastodon client (
and technically speaking a lot of it actually _is_ a Mastodon client, using the Mastodon APIs of
Friendica), adding on top of it some Friendica specific features such as groups and circles
management. With this respect, it takes a different approach compared to other existing solutions
like the very feature-complete [Relatica](https://gitlab.com/mysocialportal/relatica) app, in trying
to look familiar to users with some UI they are already accustomed to.

This project is all about experimenting and learning, so please be _patient_ if you find some bugs
or missing features.

## Development roadmap

The app is under ongoing development, here is a list of the features that are being implemented:

- [x] timeline view (Public/Local/Subscriptions) with ability to switch feed type
- [x] post detail, i.e. opening a conversation or reply in its context
- [x] user detail with ability to see posts/post and replies/pinned/media posts
- [x] login/logout to one's own instance
- [x] view notifications and check for unread items
- [x] view one's own profile
- [x] see trending posts/hashtags/news
- [x] see following recommendations
- [x] view all the posts containing a given hashtag
- [x] view followers and following of a given user
- [x] follow/unfollow other users
- [x] post actions (re-share, favorite, bookmark)
- [x] follow/unfollow hashtags
- [x] view list of one's own favorites
- [x] view list of one's own bookmarks
- [x] view list of one's own followed hashtags
- [x] view people who added as favorite/re-shared a given post
- [x] enable/disable notifications for other users
- [x] create a post/reply with image attachments and alt text
- [x] global search
- [x] open groups in "forum mode" (Friendica-specific)
- [x] delete one's own posts
- [x] edit one's own posts
- [x] mute/unmute user and see list of muted users
- [x] block/unblock user and see list of blocked users
- [x] OAuth and HTTP basic authentication modes
- [x] pin/unpin status to profile
- [x] manage one's own circles/following lists
- [x] polls (read-only, since the submit vote method is unimplemented in the backend)
- [x] manage follow requests
- [x] support post spoiler text (in post editor)
- [ ] support post title (in post editor)
- [ ] multi-account
- [ ] edit one's own profile
- [ ] private conversations
- [ ] advanced media visualization (image carousels, videos, GIFs, etc.)
- [ ] custom emojis
- [ ] scheduled posts

## Technologies used

- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform) for UI
- [Room](https://developer.android.com/kotlin/multiplatform/room) for local persistence
- [Koin](https://insert-koin.io/) for dependency injection
- [Voyager](https://voyager.adriel.cafe/) for navigation
- [Ktor](https://ktor.io/) and [Ktorfit](https://foso.github.io/Ktorfit) for networking
- [Lyricist](https://github.com/adrielcafe/lyricist) for l10n
- [Multiplatform settings](https://github.com/russhwolf/multiplatform-settings) for encrypted
  preferences
- [MaterialKolor](https://github.com/jordond/MaterialKolor) for custom theme generation
- [Ksoup](https://github.com/MohamedRejeb/Ksoup) for HTML parsing

## Disclaimer

> [!WARNING]
> This is an experimental project and some technologies it is build upon are still in pre-production
> stage, moreover this is a side-project developed by volunteers in their spare time, so use
> _at your own risk_.

You shouldn't expect a full-fledged and fully functional app; you should be prepared to occasional
failures, yet-to-implement features and areas where (a lot of) polish is needed. Contributions are
welcome and new feature requests (outside the agreed roadmap) will be evaluated depending on the
available time.
