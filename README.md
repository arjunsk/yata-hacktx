# Yet Another Tone Analyser - YATA

## Inspiration
Likewise R&D Opportunity + Halloween weekend fun!

## What it does
We intended to build a platform that emits customer emotion & facilitate necessary action based on their speech.

## How we built it
We build it using
* Kinesis for Data Stream
* Flink for Stream Processing
* ParallelDots for Emotional Analysis
* webkitSpeechRecognition for Speech to Text Conversion

## Challenges we ran into
* We were new to Google NLP modules, causing technical hiccups here and there. So we shifted to webkitSpeechRecognition.
* Emotion Analysis using AutoML also posed some challenges. So we shifted to ParalleDots.

## Accomplishments that we're proud of
* E2E flow was completed just a few hours before the end.
* Project Naming
* Project Structure
* Tech choices

## What we learned
* Google API's are something to be explored before a hackathon
* Some things need a simpler solution

## What's next for YATA
Scaling the code for a large user base.