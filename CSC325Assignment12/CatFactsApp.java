package CSC325Assignment12;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//Used HttpURLConnection
public class CatFactsApp{
   
   public void getCatFact() throws IOException {
    URL url = new URL("https://catfact.ninja/fact");
    HttpURLConnection connect = (HttpURLConnection) url.openConnection();
    connect.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();
    while ((inputLine = in.readLine()) != null){
        response.append(inputLine);
    }
    in.close();
    System.out.println(response.toString());
    }
}