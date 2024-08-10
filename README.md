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

The project is heavily inspired by the **RaccoonForLemmy** app for Lemmy, which demonstrated something
like this can be achieved. This is why it retains part of its name, making clear that the two projects
are related and share a common heritage.

## Development roadmap

The app is under ongoing development, here is a list of the features that are being implemented:

- [x] timeline view (Public/Local/Subscriptions) with ability to switch feed type
- [x] post detail, i.e. opening a conversation or a reply in its context
- [x] user detail with ability to see posts/post and replies/pinned/media posts
- [x] login/logout to one's own instance
- [x] view notification list
- [x] view one's own profile
- [x] see trending posts/hashtags/news
- [x] see following recommendations
- [x] view all the posts containing a given hashtag
- [x] view followers and following of a given user
- [ ] follow/unfollow an account
- [ ] post actions (re-share, favorite, bookmark)
- [ ] advanced media visualization (videos, GIFs, etc.)
- [ ] enable/disable notifications for an account
- [ ] edit one's own profile
- [ ] create a post/reply
- [ ] open a user (group) detail in "forum mode"
- [ ] manage one's own circles

<div align="center">
  <img width="310" src="https://github.com/user-attachments/assets/6b01d961-4fe6-4a44-8c3f-2fb5185656fa" alt="timeline screen" />
  <img width="310" src="https://github.com/user-attachments/assets/3c6b96d2-58f7-42ae-9837-14d9ea9609c2" alt="account detail screen" />
  <img width="310" src="https://github.com/user-attachments/assets/316b7a93-f46f-42f7-90fd-5efe223bbdd6" alt="post detail screen" />
  <img width="310" src="https://github.com/user-attachments/assets/2920e28d-d3e1-4d43-b8a2-3023302575d2" alt="settings screen" />
  <img width="310" src="https://github.com/user-attachments/assets/175fb882-9b66-4a5b-9b1f-220f1d9886ee" alt="explore screen (hashtags)" />
  <img width="310" src="https://github.com/user-attachments/assets/1cfe3f3e-8725-4c3e-b583-8e8cf004d259" alt="notifications screen" />
</div>

#### Disclaimer

This is an experimental project and some technologies it is build upon are still in pre-production
stage, moreover this is a side-project developed by volunteers in their spare time, so use _at your
own risk_, please don't expect a full-fledged and fully functional app and be prepared to occasional
failures and yet-to-implement features.

Please be willing to contribute if you can, requests and reports will be evaluated depending on the
time available.

