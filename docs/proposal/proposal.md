# School of Computing &mdash; Year 4 Project Proposal Form

> Edit (then commit and push) this document to complete your proposal form.
> Make use of figures / diagrams where appropriate.
>
> Do not rename this file.

## SECTION A

|                     |                   |
|---------------------|-------------------|
|Project Title:       | Facial Recognition App            |
|Student 1 Name:      | Andrew Nechifor            |
|Student 1 ID:        | 16415432            |
|Student 2 Name:      | Mahjabeen Soomro            |
|Student 2 ID:        | 17362496            |
|Project Supervisor:  | Suzanne Little            |

> Ensure that the Supervisor formally agrees to supervise your project; this is only recognised once the
> Supervisor assigns herself/himself via the project Dashboard.
>
> Project proposals without an assigned
> Supervisor will not be accepted for presentation to the Approval Panel.

## SECTION B

> Guidance: This document is expected to be approximately 3 pages in length, but it can exceed this page limit.
> It is also permissible to carry forward content from this proposal to your later documents (e.g. functional
> specification) as appropriate.
>
> Your proposal must include *at least* the following sections.


### Introduction

Our fourth year project involves developing a facial recognition app that detects and stores a user’s faces.

### Outline

The user will enter the application by tapping the application icon, which will open a camera and allow them access into the app after taking a picture of their face. The application will be able to perform this when the user is both wearing a mask on and with a mask off. The user will be able to utilize their camera to take pictures of their face and store their face and the images into the database of the application. Our facial recognition model will be developed, trained and tested using a neural network. We will be using a large dataset of thousands of images that contain pictures of faces with both masks worn and masks off. The application will analyse this dataset and output the results of this analysis.

### Background

The rise of usage of masks given this year's globel pandemic, we decided to do something related to this idea. We were also interested in machine learning and neural networks, and began to ponder how to incorporate masks and machine learning into one project. This was when the idea of facial recognition came about and this was the idea we came up with.

### Achievements

Ability of application to use the camera of users device and detect whether the user has a mask on or not. Training neural networks to be able to detect faces, if a face has a mask covering if a mask is being worn correctly or incorrectly.

### Justification

This application would be useful as masks nowadays have become a necessary item to wear when exposed to external entities and elements. This application will test datasets and provide users with the corresponding statistics. The application will analyse this dataset and output the results of this analysis i.e: how many images had masks on or off, which images had masks worn correctly or incorrectly and the occurrences of each individual mistake when a mask was worn incorrectly. 

### Programming language(s)

Java, Python

### Programming tools / Tech stack

Tensorflow, DCU’s Gitlab, Android Studio, Eclipse, AWS Databases, Mocha.js

### Hardware

PC, Linux, Windows, DCU’s Gitlab, Android phone for testing, Amazon Web Services

### Learning Challenges

We will have to learn how to use Android Studio and the Android Studio Logcat in order to create and test our mobile application. As well as that, we will have to understand how to create and develop a neural network that can perform facial recognition on large dataset accurately. We will need to learn how to handle difficulties that facial recognition can face such as issues with poor lighting, strange angles and obstructed facial features from masks. Any additional features added at the end of our project may prove to be a learning challenge for us as well.

### Breakdown of work

App Development - Mahjabeen will implement the front end to which Android Studio will be used to design and create a layout of the app and will be coded in the corresponding language, Java. 

Creating a neural network - Andrew will be creating the facial recognition model and the neural network using the Tensorflow libraries. The datasets that will train and test the neural network will have thousands of images with both masks on and off. The links to the datasets can be found below.

Setting up a database - Mahjabeen and Andrew will both work on setting up the database. Amazon Web Services will provide us with the necessary non-relational database required for this application. The AWS servers will communicate with both the front end and the back end to ensure that the application is fully functional.

Testing - We will be using Mocha.js to create the test suites that will provide the software testing for the neural network and facial recognition model, the functional testing, unit testing and user testing. To track the performance of the app, we will use Android Studio Logcat. This will display for example programming errors and it will make debugging easier.  

