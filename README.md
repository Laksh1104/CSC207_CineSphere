# CSC207_CineSphere
CSC207 Team Project

## TMDB API Key

Create a file named `.env` in the project root directory containing an API key for TMDB and your API Read Access Token:
```env
TMDB_API_KEY=[YOUR API KEY HERE]
TMDB_BEARER_TOKEN=Bearer [YOUR TOKEN HERE]
```

You can get an API key and the API Read Access Token from https://www.themoviedb.org/settings/api

## MOVIEGLU API Key
Add you MOVIEGLU Evaluation credentials as shown below which you caan request at https://api-registration.movieglu.com/.

MOVIEGLU_API_VERSION=[API_VERSION]
MOVIEGLU_AUTHORIZATION=[AUTHORIZATION]
MOVIEGLU_CLIENT=[CLIENT]
MOVIEGLU_X_API_KEY=[API_KEY]
MOVIEGLU_TERRITORY=[TERRITORY]

If you are using the sandbox data, set territory to XX and in file CinemaDataAccessObject, change **.add("geolocation", get_geolocation())** to **.add("geolocation", TEST_GEOLOCATION)** in buildHeaders().
  
