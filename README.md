# VoteBoat

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Rock the Vote is an application that provides election data at a local, state, and federal level based on the user's location. The goal of the application is encourage voter turnout by facilitating access to election information such as current Government representatives, running candidates, polling location, and election dates/times.

### App Evaluation
- **Category:** Productivity, Navigation, Lifestyle
- **Mobile:** 
    - Uses user location
    - Navigation feature is uniquely mobile
- **Story:** facilitates users' participation in elections at all governmental levels by providing factual information
- **Market:** young/older voters (historically low voter turnout/most likely to use social media applications for news)
- **Habit:** Users can browse through upcoming elections, set/receive reminders for polling times, and check polling locations near them
- **Scope:** depending on available information, Rock the Vote will start in only certain locations but has the potential to expand to a nationwide (USA) level.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can sign up + login 
* User can browse upcoming elections
* User can see detailed election information
    * Can click to view races
    * Polling dates/times/locations 
        * Registration
        * Early voting
        * Election day
        * Absentee ballots
    * User can click to view nearest locations on map
        * Show polling location/details w/ respect to user
* User can see races for a specific election
    * Candidates w/ links to pages (clickable)
* User can star/unstar election
* User can view a list of to do's for each starred election
    * User can check off to do item
* User can see a list of current representatives and their information
* User can set/receive reminders for starred elections
* User can upload a profile picture
    * Alternatively: share picture on social media

Ideas for algorithm:
* Get nearest polling place
* Most relevant races
    * Based on # of candidates running
    * When candidates are of different parties
    * At what level of government they are

**Optional Nice-to-have Stories**

* Connect to Google Calendar
    * Allows user to congregate all their events & check conflicts
* Routing & Scheduling
    * Show nearest polling station
* Show detailed polling location info
* Check of elections/track progess 
    * Almost gamifying voting
* Share on social media
* Add registration deadlines + reminders
* State requirements/id requirements

### 2. Screen Archetypes

* Login + Registration Screen
    * User can sign up + login 
* Stream
    * User can browse upcoming elections
    * User can star/unstar election
* Election Detail
    * Detailed election information
* Race Detail
    * User can see races for a specific election
* To Do
    * User can view a list of to do's for each election
    * User can check off to do item
    * User can see a list of current representatives and their information
* Map View
    * User can click to view nearest locations on map
* Profile
    * User can set/receive reminders for starred elections
    * User can upload a profile picture
    * User can type in address to use in query for elections


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream (Home Feed)
* Calendar
* Profile

**Flow Navigation** (Screen to Screen)

* Login + Registration Screen
   => Stream
* Stream
    => Election Detail
* Election Detail
    => Map View
    => Race Detail
* Race Detail
    => None
* Calendar 
    => Event Detail
* Event Detail **Optional**
    => Election Detail
* Map View
    => **Optional:** Polling Detail
* Profile
    => **Optional:** Settings
    
## Wireframes
##### **Note: **Wireframe uses a different name
<img src="wireframe.jpg" width=600>

### Interactive Prototype
[Figma Prototype](https://www.figma.com/file/wIlXR1uDPIofdl1Rl2WvwW/iElection?node-id=0%3A1)

## Data Models

#### User
| Property  | Type |  Description | 
| --- | --- | ---|
| objectId | String | unique identifier for Parse |
| createdAt | DateTime | default field |
| updatedTime | DateTime | date when post is last updated (default field) |
| username | String |  |
| elections | Array | Ids of elections that the user has starred | 
| calendarEvents | Array | Pointers to events that the user will be notified of |

#### Election
| Property  | Type |  Description | 
| --- | --- | --- |
| objectId | String | unique identifier for Parse |
| createdAt | DateTime | default field |
| updatedTime | DateTime | date when post is last updated (default field) |
| googleElectionId | String | unique identifier for Google civic API |
| electionDate | DateTime | date of the election |
| candidates | Array | pointers to candidate to all candidates running in the election |
| earlyPolls | Array | pointers to Poll objects for early polls |
| electionDayPolls | Array | pointers to Poll objects for election day |
| absenteeBallotLocations | Array | pointers to Poll objects to drop off absentee ballots |
| registrationLink | String | url to register for the election | 


#### Candidate

| Property  | Type |  Description | 
| --- | --- | --- |
| objectId | String | unique identifier for Parse |
| createdAt | DateTime | default field |
| updatedTime | DateTime | date when post is last updated (default field) |
| candidateId | String | unique identifier for Google civic API|
| name | String | name of candidate|
| party | String | candidate's running party |
| websiteUrl | String | link to candidate's website|
| parseElectionId | String | election id in Parse for which the candidate is running for|
| googleElectionId | String | election id (Google API) for which the candidate is running for|


#### Poll
| Property  | Type |  Description | 
| --- | --- | --- |
| objectId | String | unique identifier for Parse |
| createdAt | DateTime | default field |
| updatedTime | DateTime | date when post is last updated (default field) |
| location| String | full address for polling location |
| datesOpen | String | dates for which the polling location is available for |
| openTime | DateTime | opening time of location|
| closingTime | DateTime | closing time of location |
