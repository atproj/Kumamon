# Kumamon
![image](https://github.com/user-attachments/assets/17a58819-1388-4cd6-8eeb-5b14706f4386)

Kumamon is a mascot that is the minister of tourism and happiness in Kumamoto prefecture of Japan.  

This app uses a fine-tuned model of gpt to converse with users as Kumamon.  Enjoy talking to Kumamon
by asking about his favorite sport.  Or generally, ask for recommendations while you're in Kumamoto.  If you want to see pictures of what you're talking about, the app leverages DALL-E to create AI images.  You can even learn japanese and
listen to pronounciations using google text to speech.


<img src="https://github.com/user-attachments/assets/16d04d4e-582c-44ae-858d-eb54641e63d1" width="440" />

## Technical Summary
Our app was written using kotlin multiplatform, android jetpack, and kotlin flow.  

Client agnostic portions like the domain and model are placed in The "Shared" module.  Android specific code like
viewmodel and composables are in the "AndroidKuma" module.


My implementation uses a unidirectional flow of data observed by the view
![image](https://github.com/user-attachments/assets/27af9557-4902-4080-89ed-980229ae3d37)

## App Run Requirements
In order to run the app you need a pre-trained kumamon model and be running Android 7.0 or later.

## Testing
Test the app using the gradle wrapper.  On a command line at the project root execute ./gradlew testDebug and open the generated report 
PROJECT_ROOT/androidKuma/build/reports/tests/testDebugUnitTest/index.html to review a breakdown of test results.

## Future Improvements
OpenAI charges api use fees to use its model, so I want to train a free model and swap out OaiModel to see if it still performs.


Offline support could be added by leveraging Room and adding a Repository to manage separate data sources.


