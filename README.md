<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?logo=kotlin" />
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

<div align="center">
  <img width="310" src="https://github.com/user-attachments/assets/6b01d961-4fe6-4a44-8c3f-2fb5185656fa" alt="timeline screen" />
  <img width="310" src="https://github.com/user-attachments/assets/3c6b96d2-58f7-42ae-9837-14d9ea9609c2" alt="account detail screen" />
  <img width="310" src="https://github.com/user-attachments/assets/316b7a93-f46f-42f7-90fd-5efe223bbdd6" alt="post detail screen" />
  <img width="310" src="https://github.com/user-attachments/assets/2920e28d-d3e1-4d43-b8a2-3023302575d2" alt="settings screen" />
  <img width="310" src="https://github.com/user-attachments/assets/175fb882-9b66-4a5b-9b1f-220f1d9886ee" alt="explore screen (hashtags)" />
  <img width="310" src="https://github.com/user-attachments/assets/1cfe3f3e-8725-4c3e-b583-8e8cf004d259" alt="notifications screen" />
</div>

## Why was the project started?

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
to look familiar to users with some UI they are intuitively familiar with.

This project is all about experimenting and learning, so please be _patient_ if you find some bugs
or missing features.

## Development roadmap

The app is under ongoing development, here is a list of the features that are being implemented:

- [x] timeline view (Public/Local/Subscriptions) with ability to switch feed type
- [x] post detail, i.e. opening a conversation or reply in its context
- [x] user detail with ability to see posts/post and replies/pinned/media posts
- [x] login/logout to one's own instance
- [x] view notification list
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
- [ ] edit one's own posts
- [ ] delete one's own posts
- [ ] edit one's own profile
- [ ] block/unblock user and see list of blocked users
- [ ] mute/unmute user and see list of muted users
- [ ] open a user (group) detail in "forum mode" (Friendica-specific)
- [ ] manage one's own circles (Friendica-specific)
- [ ] manage one's own lists
- [ ] see private conversations
- [ ] advanced media visualization (videos, GIFs, etc.)
- [ ] custom emojis

#### Disclaimer

This is an experimental project and some technologies it is build upon are still in pre-production
stage, moreover this is a side-project developed by volunteers in their spare time, so use _at your
own risk_, please don't expect a full-fledged and fully functional app and be prepared to occasional
failures and yet-to-implement features.

Please be willing to contribute if you can, requests and reports will be evaluated depending on the
time available.

