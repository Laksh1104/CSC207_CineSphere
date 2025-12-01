package data_access;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.search_film.SearchFilmDataAccessInterface;

public class SearchFilmDataAccessObject implements SearchFilmDataAccessInterface {
    private static final String API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmYjQ3NTdjZWNmMTdjNDQyMDcyM2M0NTdhYWNkNjFlNiIsIm5iZiI6MTc2Mjc5NDA2My4xNjMsInN1YiI6IjY5MTIxYTRmMGZmMTVkYTY4NDlhYzQ3YyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.bUPbgDcky9nR63moe3ftxhKkuEQPJ-bB0F5qmL2AUfo";
    private final OkHttpClient client = new OkHttpClient();

    public int searchFilmId(String query) {
        try {
            boolean isNumeric = query.matches("\\d+");
            String url;
            if (isNumeric) {
                url = "https://api.themoviedb.org/3/movie/" + query + "?language=en-US";
            } else {
                url = "https://api.themoviedb.org/3/search/movie?query=" + query.replace(" ", "%20") + "&language=en-US";
            }

            Request request = (new Request.Builder()).url(url).addHeader("accept", "application/json").addHeader("Authorization", API_TOKEN).build();
            try (Response response = this.client.newCall(request).execute()) {
                ResponseBody body = response.body();
                if (body == null) return -1;
                String responseBody = body.string();
                if (response.isSuccessful()) {
                    if (isNumeric) {
                        JSONObject json = new JSONObject(responseBody);
                        return json.getInt("id");
                    }

                    JSONObject json = new JSONObject(responseBody);
                    JSONArray results = json.getJSONArray("results");
                    if (results.isEmpty()) {
                        return -1;
                    }

                    return results.getJSONObject(0).getInt("id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}


