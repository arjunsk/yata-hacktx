# Yet Another Tone Analyser - YATA


## Run
1. Update API in [SentimentalStub.java](https://github.com/arjunsk/yata-hacktx/blob/a05da0584fc45c460b2f891eab54eac408d16791/yata-sentence-processor/src/main/java/com/drykode/yata/processor/sentence/utils/SentimentalStub.java#L16) from [ParalleDots](https://www.paralleldots.com/)
2. Run `mvn clean install`  
3. Run [SentenceProcessorDriver.java](https://github.com/arjunsk/yata-hacktx/blob/master/yata-sentence-processor/src/main/java/com/drykode/yata/processor/sentence/SentenceProcessorDriver.java) in IDE. Note that, you might need to select maven profile, `run-in-ide`.

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
