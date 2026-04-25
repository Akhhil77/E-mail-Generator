package com.email.email_writer.Service;


import com.email.email_writer.Dto.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.util.JSONPObject;

@Service
public class EmailGeneratorService {

    private final WebClient  webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient webClient){
         this.webClient = webClient;
    }


    public String emailResponse(EmailRequest emailRequest){

          // build a prompt
         String prompt  = generateEmailPrompt(emailRequest);

         //craft a request body
         String requestBody = buildRequestBody(prompt);

         // sent request and get response
        String response = sentRequest(requestBody);

         // extract response and return
        return extractResponseContent(response);

     }


   // Generating and structuring the prompt based on our requirement , here email generation
    private String generateEmailPrompt(EmailRequest emailRequest) {

          StringBuffer prompt = new StringBuffer();
          prompt.append("Generate a professional email replay for the following email content . please don't generate a subject line ");
          if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
                prompt.append("please use ").append(emailRequest.getTone()).append(" tone ");
          }
          prompt.append("\nOrginal email : \n").append(emailRequest.getEmailContent());
          return prompt.toString();
    }


   // build the google gemini request body structure
    private String buildRequestBody(String prompt) {

        JSONObject textObj = new JSONObject();
        textObj.put("text" , prompt);

        JSONArray parts = new JSONArray();
        parts.put(textObj);

        JSONObject contentObj = new JSONObject();
        contentObj.put("parts" , parts);

        JSONArray contentArray = new JSONArray();
        contentArray.put(contentObj);

        JSONObject root = new JSONObject();
        root.put("contents" , contentArray);

        return root.toString();
    }
   // crafting a request structure inorder to sent to gemini
    public String sentRequest(String requestBody){

         return webClient.post().uri(geminiApiUrl+geminiApiKey)
                 .header("Content-Type" , "application/json")
                 .bodyValue(requestBody)
                 .retrieve()
                 .bodyToMono(String.class)
                 .block();

    }

    // extracting the text response from gemini response structure
    private String extractResponseContent(String response) {

        try{
            JSONObject root = new JSONObject(response);
            return root.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        }
        catch(Exception e){
             return "Error processing request : "+e.getMessage();
        }
    }
}
