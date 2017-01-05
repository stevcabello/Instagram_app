# Instagram_app #

Is an ***Instagram-like android app*** (with some new functionalities) developed for educational purposes by using the Instagram API and Bluetooth technology.

The app provides the same tabs that the real Instagram has: UserFeed, Discover, Photo, Activity and Profile. 
Nevertheless, some of them have limited functionalities due to restrictions of the Instagram API (it doesn't allow POST) 

For instance, the leave of Likes and Comments is not allowed, but I have *simulated* these functionalities, and anyway showing the error that the Instagram API returns when I want to leave a Like or Comment.

And also some calls only return a limited amount of information i.e. if a post has 400 comments I only can see the last 8 comments.


However, 2 other functionalities have been added (in the UserFeed tab).

## New Functionalities ##

**1. Sort userfeed by location**

  A toggle button placed in the toolbar allows the user to sort the posts by date/time (as Instagram does) or by Location        (showing the closets posts on top of the list) 


**2. Share a post to a Bluetooth-in range available device** ..(***See Note***)

  By swipping the photo of a post we can select a Bluetooth device near to us and share the post with that device.
  The shared post will appear first on the userfeed with the tag ***In Range***, to indicate that this post was received via     Bluetooth.


## Various tidbits ##

### Access to information ###
The access to data is through the Instragram API ***(no web services or external databases were created)***


### People Suggestion ###
The algorithm for suggestion of people basically consists on select friends of my friends with whom I have likes in common. i.e. Likes to the same posts


### Photo Filters ###
  Only 3 dummy filters are provided. And the user can change the brigth and contrast of the image.


### Upload Post ###
Due to Instagram API restrictions, the post of a picture or photo is not allowed. A ***Share button*** is used to accomplish this task.



**Note:** 
There may be some problems with Android 5.0


## DEMO VIDEO ##
https://youtu.be/s3KGtHUhYQk

https://youtu.be/ZdfwFVQK7hw


  
