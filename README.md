## Nausicaa

### What is Nausicaa

Nausicaa is a command line tool to upload your photos to [Flickr](http://flickr.com/).

After uploading, you will get the static path of all sizes of the photo:

![image](http://farm8.staticflickr.com/7190/6847181395_efcfcd49ce_b.jpg)

The name Nausicaa is from the character of [Hayao Miyazaki](http://en.wikipedia.org/wiki/Hayao_Miyazaki)'s famous animation [Nausicaa of the Valley of the Wind](http://en.wikipedia.org/wiki/Nausica%C3%A4_of_the_Valley_of_the_Wind_%28film%29).

### Environment

* Unix-like System.
* JRE 6 above.

### How to use

1. Download nausicaa.jar
2. Open a terminal
3. Run `java -jar nausicaa.jar`, and you all get this
> It seems that Nausicaa is not setted up, would you like to set up(yes to setup, other to exit):
4. type `yes` to setup. And you all get:
> Access the follow URL by your favorite web brower:http://www.flickr.com/services/oauth/authorize?oauth_token=72157629245928175-5b821abfc62f79f5
5. Just follow the step, access the url from your web browser, input the code you get.
6. Shortly you'll see:
> Set up complete!
7. Now you can upload photo now, just run `java -jar nausicaa.jar [path-to-the-photo]`

### Extra options

Nausicaa provide some extra option, you can run `java -jar nausicaa.jar [path-to-the-photo] [option_key]=[option_value]` to add extra options.

Here is the extra options provided:

Key   | Comment 
----- | ------- 
title | the title of the photo(Nausicaa will use the file name of the photo by default.).
description | the description of the photo.
tags  | the tags of the photo, multiple tags separated by commas.
safetyLevel | The safety level of the photo, you could specify one of the following three value as safety level:safe, middle, restricted
contentType | The content type of the photo, you could specify one of the following three value as content type:photo, screenshot, other
