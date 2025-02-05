---
layout: default
---

Welcome to the homepage of Raccoon for Friendica!

# Table of contents

- [project overview](#overview)
- [rationale](#rationale)
- [key app features](#key-app-features)
- [acknowledgements](#acknowledgements)
- [further reading](#further-reading)

# Overview

Raccoon is a mobile client for [Friendica](https://friendi.ca), a federated social platform. The
app is powered by Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP), mainly focusing on the
Android platform, even if it could run on iOS too and – who knows? – in the future new platforms
could be added (such as desktop).

Like the "twin" Lemmy app, this project started out as an exercise to experiment with KMP and CMP,
push them to the limit with some real world task with a fair amount of complexity (image loading,
HTML rendering, file system and gallery access, networking, local database management,
shared preferences, navigation, resource access e.g. fonts/drawables/localization, etc.), and it
quickly grew to a fully functional app that can benefit other users too, this is the reason why it
is released as an open-source app.

# Rationale

Friendica is an _extremely powerful_ and _feature-complete_ platform, it has some distinctive
features that other widely diffused Fediverse platform do not have yet (e.g. **groups**, **direct
messages**, **media gallery**, the possibility to organize contacts in **circles** to
read/publish contents, an integrated **event calendar**, etc.) so it deserves to really stand out in
the Fediverse.

Unfortunately, using the web app in a mobile device is not entirely optimal (because the web UI is
very "dense" with information which hardly fit on a mobile screen); and there is no mobile
application that supports all of its features in a user-friendly way:

- most Fediverse apps (e.g. [Moshidon](https://github.com/LucasGGamerM/moshidon)) revolve around
  Mastodon, so they support Friendica only in that subset of features that it shares with Mastodon;
- some Friendica-specific apps like [Dica](https://github.com/jasoncheng/dica)
  or [Friendiqa](https://git.friendi.ca/lubuwest/Friendiqa) are quite old and unmaintained;
- [Relatica](https://gitlab.com/mysocialportal/relatica) looks very promising, it is modern and
  well maintained and it has received some visibility from important international foundations, but
  it is written using a programming language I don't know so I could not contribute to it in any
  way.

Raccoon tries to combine the feature-completeness of Relatica with the well-polished UI of Moshidon,
providing a Friendica user with everything they need from their mobile device (more complex features
such as content moderation for admins will only be possible via the webapp) and presented in a
familiar way like most social clients.

# Key app features

Here is a list of the most important features of the app:

- timeline view with ability to switch feed type (public, local, subscriptions, user-made lists);
- post detail, i.e. opening a conversation in its context and see the replies, number of re-shares
  and people who added it to favorites;
- user detail with ability to see posts, post and replies, pinned posts and media, subscribe for
  notifications from a user, follow/send a request or unfollow them, see all following/followers of
  a given user;
- support for ActivityPub groups (i.e. distribution lists) and opening forum-like (or Lemmy-like)
  mode;
- see trending posts, hashtags, news and following recommendations;
- follow/unfollow an hashtag and view all the posts containing a given hashtag;
- post actions (re-share, favorite, bookmark) and – for own ones – edit, delete or pin to profile;
- global search hashtags, post and users containing some specific terms;
- customize the application appearance with color themes, font sizes, etc;
- login using OAuth2 (or, on Friendica, legacy login with username and password);
- view and edit one's own profile data (custom fields and images not supported yet by the back-end);
- view incoming notifications and filter the list of notifications;
- manage one's own follow requests and accept/reject each one of them;
- view the list of one's own favorites, bookmarks and followed hashtags;
- create a post/reply with image attachments (and alt text for each attachment) a spoiler, a title
  (only on Friendica) and write formatted content in HTML, BBCode (on Friendica) or Markdown (on
  Mastodon);
- schedule a post for a future date or save it to local drafts;
- report posts/users to administrators for content moderation;
- mute/unmute, block/unblock users and manage the list of muted/blocked users;
- manage one's own circles (user-defined lists), which on Friendica can also be used as a publishing
  target;
- see polls in read-only mode (voting is not supported by the back-end yet);
- multi-account with easy ability to switch between accounts (and, in anonymous mode, to switch
  instance);
- send direct messages (only on Friendica) to other users and see conversations;
- manage one's own photo gallery (only on Friendica);
- view one's own calendar (only on Friendica) in read-only mode (creating events is not supported by
  the back-end yet).

# Acknowledgements

A special thanks goes to all those who contributed so far:

- [informapirata](https://poliverso.org/profile/informapirata)
- [N7-X](https://github.com/N7-X)
- [pvagner](https://github.com/pvagner)
- [toas-koas](https://github.com/toas-koas)
- [gnu-ewm](https://hosted.weblate.org/user/gnu-ewm)
- [ktlinux](https://hosted.weblate.org/user/ktlinux)
- [Fitik](https://hosted.weblate.org/user/11mancookie11)
- [TamilNeram](https://github.com/TamilNeram)
- [Ricky Tigg](https://hosted.weblate.org/user/tigg)
- [gregscoor](https://github.com/gregscoor)
- all those who reported feedback and ideas through Friendica, Mastodon, GitHub, emails,
  private messages, homing pigeons and every possibly imaginable medium.

This project would not be what it is were it not for the huge amount of patience and dedication of
these early adopters who sent me continuous feedback and ideas for improvement after every release,
reported bugs, offered to help, submitted translations in their local language, offered help to
other users and made the community a better place, etc.

You are **awesome**… THANKS ❤️🦝️❤️

# Further reading

If what you have read so far sounds interesting to you and you want to know more, here are some
useful
links:

- check out the [User manual](manual/main.md);
- have a look at the
  project's [README](https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica/blob/master/README.md)
- consult
  the [CONTRIBUTING](https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica/blob/master/CONTRIBUTING.md)
  guide.

If, on the other hand, you just want to interact with the community, provide suggestions, report
bugs, contribute with translations or tell your opinion, you can:

- use the [Friendica group](https://poliverso.org/profile/raccoonforfriendicaapp) to receive updates
  about the
  new releases, participate into public discussions, share your feedback or suggestions;
- join out [Matrix room](https://matrix.to/#/#raccoonforfriendicaapp:matrix.org) to reach out to the
  developers or other users;
- use the
  GitHub [issue tracker](https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica/issues)
  to report bugs or request features;
- finally, if none of the above methods fits your needs you
  can write an [email](mailto://livefast.eattrash.raccoon@gmail.com).
