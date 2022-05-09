import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws Exception {
        File arquivo = new File("test.xml");
        FileReader ler = new FileReader(arquivo); 
        BufferedReader origem = new BufferedReader(ler);

        String[] newArquivo = arquivo.getName().split(Pattern.quote("."));
        OutputStream os = new FileOutputStream(newArquivo[0]+".json");
        Writer wr = new OutputStreamWriter(os);
        BufferedWriter br = new BufferedWriter(wr);

        String linha = null;
        Map<String, Integer> objJson = new HashMap<String, Integer>();
        Map<String, Integer> objJsonPosition = new HashMap<String, Integer>();
        Map<String, Integer> objJsonPositionFinal = new HashMap<String, Integer>();

        String regexXml = "(<.+>)(.+)(<.+>)";
        Pattern r = Pattern.compile(regexXml);
        String preJSON = "{\n";
        while ((linha = origem.readLine()) != null) {
            Matcher m = r.matcher(linha);
            if(m.find()){
                String object = m.group(1).replace("<", "").replace(">", "");
                String text = m.group(2);
                preJSON += "\""+object+"\": \""+text+"\",";
                preJSON += "\n";
            }
            else{
                String object = linha.replace("/", "").replace("<", "").replace(">", "");
                if(linha.contains("/")){
                    preJSON += "} \n";
                    
                    if(preJSON.charAt(preJSON.length() -5) == ','){
                        StringBuilder tempStrBuilder = new StringBuilder(preJSON);
                        tempStrBuilder.deleteCharAt(preJSON.length() -5);
                        preJSON = tempStrBuilder.toString();
                    }
                    objJsonPositionFinal.put(object, preJSON.length());
                }
                else{
                    if(objJson.containsKey(object)){
                        objJson.put(object, objJson.get(object)+1);
                        
                        StringBuilder tempStrBuilder = new StringBuilder(preJSON);
                        tempStrBuilder.insert(preJSON.lastIndexOf("\n")-1,",");
                        preJSON = tempStrBuilder.toString();
                        preJSON += "{ \n";
                    } 
                    else {
                        
                        preJSON += "\""+object+"\""+": {\n";
                        //System.out.println("PREJSON = " + preJSON.length());
                        Integer preJSONInt = preJSON.length() - 3;

                        objJson.put(object,1);
                        objJsonPosition.put(object,preJSONInt);
                        objJsonPositionFinal.put(object,0);
                    }
                }
            }
        }
        for (String string : objJson.keySet()) {
            //System.out.println(string+" = "+objJson.get(string));
            if(objJson.get(string) >= 2){
             StringBuilder tempStrBuilder = new StringBuilder(preJSON);
             tempStrBuilder.insert(objJsonPosition.get(string),"[");
             tempStrBuilder.insert(objJsonPositionFinal.get(string),"\n]");
             preJSON = tempStrBuilder.toString();
            }
        }
        preJSON += "}";
        br.write(preJSON);
        origem.close();
        br.close();
    }
}