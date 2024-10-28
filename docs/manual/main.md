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

- a header containing display name, username, banner and avatar, bio and custom fields;
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

## Follow requests

If in your profile settings you have enabled manual approval for follow requests, this screen
contains the list of pending follow request you have received.

For each one of the items you can either accept or reject the request.

## Node info

This screen contains some information about the current instance you are connected to:

- banner image;
- domain;
- description;
- contact account;
- list of rules members of this server have to comply with;
- backend type and version.
