# Streamer Tracker Project

## Project Summary:

The streamer tracker project is a program designed to fetch and present information to the user about specified streamers on two platforms, 
Twitch and YouTube. By using the API’s of both platforms, this project will let users search for streamers, view their live stream statuses, 
and grab data about recent streams, videos, clips, and general streamer data. 

For **Iteration 1**, our main objective is establishing a foundation for the features that allow for searching of streamers and the retrieval of basic information about them.

For **Iteration 2**, our main objective is establishing a Graphical User Interface to help enhance a user's experience.

For **Iteration 3**, our main objective is establishing a better GUI and implementing ideas that we form based on what we consider to be our next steps.

## Suppression:

**Inside ApiInitializer**
We have suppressed deprecation due to the implemented method and system we have currently that works just fine versus what may eventually come or is already deprecated inside the API. This is specifically referring to the YouTube API method.

**Inside Tests**
We have suppressed deprecation to rid warnings in implemented test code due to code that could soon change or be out of date. At the moment, the code still works.

## Needed Build Instructions:

This project runs from gradle on IntelliJ but the main program will work in most Java cases.

This build depends on API's and a stable network connection.

To run the program you must have a config.properties file in your program that contains the two tokens for the Twitch and YouTube APIs. They must be legible and may not have expired or else you may not have access to the full program.

Console-view runs from Main simply while the GUI runs from a gradle run script that implements Javafx.

---

## Authors:

- **Dominic Szpak**
- **Paul Guzman**
- **Santos Pena**
- **Christopher Vojkufka**
