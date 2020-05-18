package com.ofss.shutil;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;


@RestController
public class ShUtil {

	@Value("${shutil.script.dir}")
	private String scriptDir;
	@Value("${shutil.allowed.scripts}")
	private String allowedScripts;
	
    
	private UUID uuid;
	private void log(String txt) {
		Date date = new Date();
		System.out.println(new Timestamp(date.getTime())+ " [ " + this.uuid + " ] " + txt);
	}
	@PostMapping("/cmdStr")
    public String cmdStr(@RequestBody String cmd) {
		return execCmd(cmd).getOutput();
	}
	@PostMapping("/putf/{fn}")
    public int putf(@PathVariable String fn, @RequestBody String fileContent) {
	try {
		FileWriter fw=new FileWriter(fn.replace("~", "/"));    
		fw.write(fileContent);    
		fw.close();
		return 0;
	  }catch(Exception e){
		  log(e.getMessage());
	  return 99;
	  }    
	}
	@PostMapping("/cmd")
	public ShOutput execCmd (@RequestBody String req) {
	    String finalValue = "OK";
	    String cmd="";
	    String exp="";
		String outputstr = "";
		String args="";
		ShOutput shOutput = new ShOutput();
		try {
				JSONObject jObj = new JSONObject(req);			
				this.uuid = UUID.randomUUID();
				shOutput.setUuid(this.uuid);
				log("obj" +req);
				Iterator<String> keyItr = jObj.keys();
				log("=== " +jObj.length());
				while(keyItr.hasNext()) {
					String key = keyItr.next();
					String val=jObj.getString(key);
					log("key= " + key + " val= "+val);
					if (key.equals("cmd")) {
						cmd = val;
					}
					else if (key.equals("args")) {
						args = val;
					}
					else {
						exp = exp + " export "+key+"="+val + " && ";
			        }
				}
				if ( (","+allowedScripts+",").contains(","+cmd+",")) {
					ProcessBuilder processBuilder = new ProcessBuilder();
					processBuilder.command("bash", "-c", exp + scriptDir+"/"+cmd+".sh "+args);
					//processBuilder.command("bash", "-c", scriptDir+"/"+cmd+".sh "+args);
					Process process = processBuilder.start();
					StringBuilder output = new StringBuilder();
					BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						outputstr = outputstr + line + "\n";
					}
					int exitVal = process.waitFor();
					shOutput.setResult(exitVal);
					if (exitVal == 0) {
						log("Success!");
					} else {
						log("Error!");
					}
					shOutput.setOutput(outputstr);
				}
				else {
					shOutput.setOutput("You are not allowed to run this method");
					shOutput.setResult(99);
				}
			} catch (Exception e) {
					e.printStackTrace();
					outputstr = outputstr+e.getMessage();    		
		}
		log("Result :: " + shOutput.getResult() + "::\n" + shOutput.getOutput());
		return shOutput;
	}
	
	@RequestMapping("/ping")
	public String ping() {
		return "ok";
    }
	
}
