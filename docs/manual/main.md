# User manual

This page contains a short description of the main features of the app.

## Introduction

**Friendica** is social platform and it has all the features you would expect from a piece of
software
or this category, you can:

- see the timeline of posts created by other users;
- follow an hashtag and see all the posts containing it;
- create new top-level posts or replies to other's posts or schedule them for later (and edit or
  delete your ones);
- like or dislike posts;
- save posts to bookmarks;
- re-share posts;
- follow or unfollow other users, see their profile and subscribe for notifications about their
  activity;
- edit your own profile and accept/reject your follow requests;
- create our customized feeds;
- see trending hashtags, posts or links (and follow suggestions);
- globally search for hashtags, posts or users;
- mute or block users to filter out contents you don't like (and undo these actions);
- report posts and users to administrators, etc.

On top of that, Friendica has a series of interesting features which make it stand out among other
similar federated platforms:

- you can publish long and formatted posts (with a BBCode-based syntax) and add a title for each
  post besides the spoiler;
- it supports ActivityPub's "group" entities, i.e. special kind of accounts which automatically
  re-distribute all contents mentioning them to subscribers and work like forums;
- it has the concept of user-defined lists or _Circles_, which can also be used as a target
  for publication (outbound) and not only as customized feeds (inbound);
- direct messages: you can directly send a message to one of your followed users and have a private
  conversation;
- image gallery: you can upload photos and organize them into albums, moreover you can easily
  insert them into your posts taking them from the catalog;
- event calendar: you can create events and make them visible to your followers or see the events
  that have been shared with you;
- it supports quoting posts inside other posts (AKA _cross-posting_);
- you can import RSS feeds so that you can follow them as regular accounts and re-share their posts;
- you can delegate the management of an account to one or more other accounts and easily create
  independent accounts;
- and, of course, [much more](https://friendi.ca/about/features).

All these features can be accessed using the official web application, but a lot of them are also
open to third-party apps using their public APIs, Raccoon intends to do just that: be a client for
Friendica which makes it easier to use it from a mobile device.

## Application UI general structure

The application UI is divided into three parts:

- the navigation drawer (accessible from the hamburger menu in the top-start corner or with a swipe
  gesture from the start direction), containing your user and instance name if you're logged or just
  the instance name in anonymous mode and a series of shortcuts to different app sections (e.g.
  settings or node info pages);
- the bottom navigation bar, containing shortcuts for the most important app sections (Timeline,
  Explore, Inbox and Profile);
- the main screen which usually contains a top bar with a title and possibly some actions in the
  top-end corner and the main content (all subsequent pages will follow this structure).

## Timeline

The timeline section contains a series of posts that belong to either a default feed or a custom
feed.

There are three default feed types:

- Local (i.e. posts that have been created or re-shared on the instance you are connecting to);
- All (i.e. posts coming from your instance plus all the federated instances);
- Subscriptions (only for logged users: posts created or re-shared by accounts you are following or
  which contain one or more hashtags you are following).

Custom feeds, on the other hand, can be:

- any of your user-defined lists;
- a channel, i.e. predefined aggregations e.g. "For you", "Discover", "Followers", "Images", etc.
  (Friendica-specific);
- any of the groups you are following (Friendica-specific);

Every item in the timeline has the following structure:

- re-share or reply indication (user who re-shared or original poster if the current one is a
  reply);
- author (avatar, display name and username of author);
- creation date;
- title (only from sources which allow it like Friendica or Lemmy);
- spoiler text (optional);
- textual content (visible if no spoiler or spoiler expanded);
- attachments (videos or images);
- card (additional preview content or external URL).

If the content of a post contains a hashtag you will be able to open the
corresponding [screen](#hashtag-feed) and if it contains a mention you will be able to open the
corresponding [profile](#user-profile).

Each post can be re-shared, added to favorites or bookmarked; you can create a reply to each of them
and see the number of replies, re-shares favorites and whether it is in your bookmarks or not.

Moreover, from each single post it is possible to:

- access the [profile](#user-profile) of all users involved (author, re-sharer or original poster);
- enter the [post detail](#post-detail) screen;

## Post detail

In this screen you will will be able to see it in its context (with ancestors until the root post
and all descendants).

Moreover you can the list of users who re-shared it or added it to their favorites.

Apart from that, this screen is very similar to a regular [timeline](#timeline), and allows to
perform the same actions and open user profiles or other post details.

## User profile

The purpose of the user profile is to display information about a specific user and access the set
of contents they have created. There are two flavours of this screen: _classic mode_ (for regular
users) and _forum mode_ (for group accounts).

### Classic mode

The screen is made up by two parts:

- a header containing display name, username, banner and avatar, number of following/followers (from
  which you can open the user list), bio and custom fields;
- the list of posts created by the user, which has the following sections:
  - **Posts** list of top-level posts by the user;
  - **Posts & replies** all posts including replies by the user;
  - **Media** posts containing media attachments;
  - **Pinned** posts pinned by the user to their profile;

Each item of these lists allows you to access its [post detail](#post-detail).

If you are logged, the headers shows the relationship your user has with this user:

- mutual followers;
- you follow them;
- they follow you;
- you sent a follow request which is pending;
- you received a follow request from them which is pending;
- no relation.

If you follow the user, you will also see the notification status (enabled or disabled).

From the top app bar action menu, you can also:

- block/unblock the user;
- mute/unmute the user (and specify for how much time);
- report the user;
- add a personal note on the user;
- switch to [forum mode](#forum-mode) (if it is a group).

### Forum mode

In forum mode, you will be presented with all the top-level posts that have been re-shard by the
group, i.e. the list of topics of this forum. This is a special kind of timeline, by tapping on each
item you will enter the [thread detail](#thread-detail).

From the top app bar action menu, you can also switch to [classic mode](#classic-mode).

## Thread detail

This screen is similar to a [post detai](#post-detail) but comments are displayed in a Lemmy-like
layout, i.e. indented according to their nesting level and with a coloured bar which makes it easier
to distinguish parent-child relationship between replies.

## Hashtag feed

The hashtag feed is a special kind of [timeline](#timeline) featuring all the posts containing a
given hashtag and it follows the same structure.

From the top app bar, you will be able to follow or unfollow the hashtag.

## Favorites and bookmarks

Favorites and bookmarks are special kinds of [timelines](#timeline) and they follow the same
structure.

The only difference is that if you un-favorite or un-bookmark a post it will immediately disappear
from the feed.

## Followed hashtags

This screen contains the list of all hashtags you are following in alphabetical order, and you can
unfollow each one of them.

Each item of this list allows to open the corresponding [feed](#hashtag-feed).

## Explore

This section allows you to see the most trending content in the instance you are connected to. The
screen is divided into the following sections:

- **Hashtags** contains the list of trending hashtags, with the number of people talking about it
  and a
  chart about its usage in the last 7 days;<sup><a href="#hashtag-usage-disclaimer">(*)</a></sup>
- **Posts** list of treding posts
- **Links** aggregated view of the URLS used most frequently in posts;
- (only for logged users) **For you** contains the list of follow suggestions for your user.

Opening a hashtag will lead you to the [dedicated feed](#hashtag-feed), opening a post to
its [detail](#post-detail) and opening a user to the corresponding [profile](#user-profile). Opening
a link will either open the external browser or custom tabs depending on the "URL opening mode"
option selected in the [app settings](#application-settings).

<p id="hashtag-usage-disclaimer">
  * note that on different backends the number or days may vary from 1 (single point) to more
</p>

## Search

This screen makes it possible to search in the Fediverse: it contains a search field to enter the
query string and a tab selector to choose the desired result type (either Posts, Users or Hashtags).

Please notice that it is not possible to search unless a non-empty query has been inserted.

Opening a hashtag will lead you to the [dedicated feed](#hashtag-feed), opening a post to
its [detail](#post-detail) and opening a user to the corresponding [profile](#user-profile).

## Inbox

This screen contains the list of notifications for the events you have subscribed to, so it is only
available for logged users.

The main content presents the list of notifications, which can be of the following types:

- **Post** a user you enabled notifications for has published a new post;
- **Edit** a post you have re-shared has been modified by its author;
- **Mention** you have been mentioned in a post;
- **Re-share** one of your posts has been re-shared;
- **Favorite** one of your posts has been added to favorites;
- **Follow** someone has started following you;
- **Follow request** someone has sent you a follow request;
- **Poll** a poll you have participated in has expired.

From the top bar menu it is possible to select/unselect specific categories of notifications to
filter the results displayed in the list.

Tapping on each item of the list, it is possible to open the [user profile](#user-profile)
or [post detail](#post-detail).

## Profile

If you are running the app in anonymous mode, the Profile screen contains the Login button to start
the authentication flow. If, on the other hand, you are already logged in, it looks similar to a
regular [user profile](#user-profile) but has some additional actions specific for your user.

If you have multiple accounts, in the top app bar you will find a "Manage account" button to switch
between one another.

### Login

There are two possible ways to login, depending on your preferences (or what your backend allows):

- **OAuth2** (recommended) web-based flow, a browser tab will be opened to log-in and a
  client-specific token allowing the app to operate on the user's behalf will be generated;
- **HTTP Basic** (legacy, available on Friendica) this in-app flows involves selecting the instance
  and inserting your credentials, all API calls will be using HTTP Basic auth header.

Needless to say – but we'll repeat it for the sake of clarity – the recommended way to login is
OAuth2 because:

- your username/password never go outside browser and remain unknown to all third-party subjects
  (including the Raccoon app);
- it has finer-grained access levels, meaning you can control the various _scopes_ each individual
  token can be used for;
- tokens can be revoked at any time, making it easier to mitigate potentially unwanted accesses.

### One's own user detail

In the header, instead of the relationship/notification buttons you will find an "Edit profile"
button to open your [profile preferences](#profile-settings).

## User list

This screen contains a generic list of users; it can be opened either from
the [post detail](#post-detail) (to see who added a post to favorites or re-shared it) or from
the [user profile](#user-profile) (to see who is following or followed by a given user). It displays
the avatar, display name and username of users plus the corresponding relationship status.

You can use the follow/send request/mutuals button to modify your relationship with the given
account.

## Follow requests

If in your [profile settings](#profile-settings) you have enabled manual approval for follow
requests, this screen contains the list of pending follow request you have received.

For each one of the items you can either accept or reject the request.

## Node info

This screen contains some information about the current instance you are connected to:

- banner image;
- domain;
- description;
- contact account;
- list of rules members of this server have to comply with;
- backend type and version.

## App information

This dialog contains more information about the app:

- version name and code;
- a link to the changelog;
- a button to open a feedback form;
- a link to the GitHub main page of the app;
- a shortcut to the Friendica discussion group for the app;
- a link to the project's Matrix room;
- the entry point for the list of licences for the libraries and resources used in the app.

## Profile settings

This screen allows you to edit your profile data and configure (to some extent) the discoverability
and visibility of your profile.

The profile data which can be edited are:

- display name;
- bio;
- avatar;<sup><a href="#user-profile-experimental-disclaimer">(*)</a></sup>
- banner;<sup><a href="#user-profile-experimental-disclaimer">(*)</a></sup>
- custom fields;<sup><a href="#user-profile-experimental-disclaimer">(*)</a></sup>
- bot (mark account as bot);
- manual approval of follow requests (`locked`);
- make account visible in searches (`discoverable`);
- make following and follower lists private (`hide_collections`);
- include posts by this account in public timeline (`indexable`).

<p id="user-profile-experimental-disclaimer">
  * depending on the back-end type these fields may not work, e.g. there are some known compatibility
issues on some versions of Friendica
</p>

## Application settings

This screen allows to customize the application appearance and behaviour, it has the following
sections:

- **General**
  - **Language**  configures the language for the user interface;
  - **Default timeline type** configures the timeline type used by default in the Timeline screen
  - **Default visibility for posts** configures the visibility (`public`, `unlisted`, `private` -
    i.e. only followers - or `direct` - i.e. ony mentions) used for posts by default;
  - **Default visibility for replies** configures the visibility used for replies by default;
  - **URL opening mode** configures how URLs will be opened (external browser or custom tabs);
  - **Exclude replies from timeline** configures whether replies are included by default in the
    Timeline screen;
  - **Open groups in forum mode** by default configures whether group accounts are going to be
    opened in forum mode (as opposed to classic mode) by default;
  - **Markup for compositing** determines the type of markup syntax used in new posts (plain text -
    i.e. no markup – BBCode – Friendica-specific – HTML or Markdown – Mastodon specific);(*)
  - **Max post body lines** configures the maximum number of lines for posts which will be shown in
    feeds;
  - **Check for notifications in background** configures the time interval between background checks
    for incoming notifications;
- **Look & Feel**
  - **UI theme** configures the color theme (light, dark or dark optimized for AMOLED screens);
  - **Font family** configures the font used in the UI;
  - **Font size** configures the scale factor applied to fonts in the UI
  - **Theme color** allows to choose a color to generate a Material 3 palette from;
  - **Material You** generate a color palette based on the launcher image;
- **NSFW**
  - **Manage filters** opens the ban and [filter management](#manage-filters) screen;
  - **Include NSFW contents** configures a client-side filter to exclude sensitive posts;
  - **Blur NSFW media** allows, if sensitive contents are included, to blur images and hide videos
    when they occur in timelines.

## Manage filters

This screen allows to revoke current restrictions on other accounts. It is divided into two
sections:

- **Muted** for muted accounts;
- **Bans** for banned ones.

## Post composer

This screen allows to create new posts or replies. The top bar contains the Submit button which can
have different icons depending on the publishing type:

- a Send icon for regular publication;
- a Save icon for drafts;
- a Schedule icon for scheduled posts;

whereas the action menu contains the following items:

- **Save draft** changes the publishing type from regular to draft;
- **Set schedule** changes the publishing type from regular to scheduled posts;
- **Insert emoji** allows to insert a custom emoji;
- **Open preview** opens a preview of the post (only if "Markup for compositing" option in Settings
  is
  _not_ plain text);
- **Add title**/**Remove title** to add or remove a title for the post;
- **Add image (media gallery)** adds an image from an album in the Friendica media gallery;
- **Insert list** adds an itemized list;
- **Add spoiler**/**Remove spoiler** (only if "Markup for compositing" option in Settings is plain
  text) to add or remove a spoiler for the post;
- **Add image** (only if "Markup for compositing" option in Settings is plain text) adds an image
  from
  the device gallery;

Below the top bar there is a header containing:

- an indication of the current user (who will be the author of the post);
- the visibility (`public`, `unlisted`, `private`, `direct` or a Friendica circle);
- the schedule date and time (for scheduled posts);
- the current character count / character limit according to instance configuration.

Afterwards you can find the main text field for the post body. In the bottom part of the screen,
only if "Markup for compositing" option in Settings is _not_ plain text, you will find a formatting
toolbar with the following buttons:

- **Add image** to add an image from the device gallery;
- **Add link** to add a hyperlink
- **Bold** to insert some text in bold;
- **Italic** to insert some text in italic;
- **Underline** to insert some underlined text;
- **Strikethrough** to insert some text with a strikethrough effect;
- Code to insert monospaced font;
- Toggle spoiler to add or remove a spoiler for the post.
