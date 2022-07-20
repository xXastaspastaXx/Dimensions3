package me.xxastaspastaxx.dimensions.addons.particles;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;

import me.xxastaspastaxx.dimensions.Dimensions;
import me.xxastaspastaxx.dimensions.DimensionsDebbuger;
import me.xxastaspastaxx.dimensions.completePortal.CompletePortal;

public class ParticlePack {

	ArrayList<String> play = new ArrayList<String>();
	HashMap<String, ArrayList<String>> fors = new HashMap<String, ArrayList<String>>();
	
	HashMap<String, Double> vars = new HashMap<String, Double>();
	
	YamlConfiguration config;
	DecimalFormat df = new DecimalFormat("#");
	
	public ParticlePack(String file) {
		df.setMaximumFractionDigits(8);
		config = YamlConfiguration.loadConfiguration(new File("./plugins/Dimensions/ParticlePacks/"+file+".yml"));
		
		for (String st : config.getStringList("start")) {
			if (st.startsWith("debug")) {
				DimensionsDebbuger.debug(replaceVars(st.replaceFirst("debug ", ""), null), DimensionsDebbuger.HIGH);
			} else if (st.contains("=")) {
				String[] var = st.split(" = ");
				vars.put(var[0], eval(var[1], null));
			}
		}
		if (!vars.containsKey("frequency")) vars.put("frequency", 20d);
		
	}
	
	
	public double eval(final String st, HashMap<String, Double> tempVars) {

		final String str = replaceVars(st, tempVars);
        try {
            return new Object() {
                int pos = -1, ch;
 
                void nextChar() {
                    ch = (++pos < str.length()) ? str.charAt(pos) : -1;
                }
 
                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }
 
                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < str.length()) throw new RuntimeException("Unable to eval: "+str);
                    return Double.parseDouble(df.format(x));
                }
 
                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if      (eat('+')) x += parseTerm(); // addition
                        else if (eat('-')) x -= parseTerm(); // subtraction
                        else return x;
                    }
                }
 
                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if      (eat('*')) x *= parseFactor(); // multiplication
                        else if (eat('/')) x /= parseFactor(); // division
                        else return x;
                    }
                }
 
                double parseFactor() {
                    if (eat('+')) return parseFactor(); // unary plus
                    if (eat('-')) return -parseFactor(); // unary minus
 
                    double x;
                    int startPos = this.pos;
                    if (eat('(')) { // parentheses
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') { // functions
                        while (ch >= 'a' && ch <= 'z') nextChar();
                        String func = str.substring(startPos, this.pos);
                        x = parseFactor();
                        if (func.equals("sqrt")) x = Math.sqrt(x);
                        else if (func.equals("sin")) x = Math.sin(x);
                        else if (func.equals("cos")) x = Math.cos(x);
                        else if (func.equals("tan")) x = Math.tan(x);
                        else throw new RuntimeException("Unknown function: " + func);
                    } else {
                        throw new RuntimeException("Unable to eval: "+str);
                    }
 
                    if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
 
                    return x;
                }
            }.parse();
        } catch (Exception e) {
        	//e.printStackTrace();
            return Double.MAX_VALUE;
        }
    }
	
	public String replaceVars(String str, HashMap<String, Double> tempVars) {
		if (tempVars!=null) {
			for (String var : tempVars.keySet()) {

				str = str.replaceAll("\\b"+var+"\\b", tempVars.get(var)+"");
			}
		}
		for (String var : vars.keySet()) {

			str = str.replaceAll("\\b"+var+"\\b", vars.get(var)+"");
		}
		return str;
	}

	public static ArrayList<ParticlePack> load(List<String> stringList) {
		ArrayList<ParticlePack> res = new ArrayList<ParticlePack>();
		for (String s : stringList) {
			res.add(new ParticlePack(s));
		}
		return res;
	}
	
	public void runPortal(CompletePortal complete, Location loc) {
		if (config.contains("portal"))
			Bukkit.getScheduler().runTaskAsynchronously(Dimensions.getInstance(), new Runnable() {
				public void run() {
					ParticlePack.this.run("portal", complete, loc);
				}
			});
	}
	
	public void runTile(CompletePortal complete, Location loc) {
		if (config.contains("tile"))
			Bukkit.getScheduler().runTaskAsynchronously(Dimensions.getInstance(), new Runnable() {
				public void run() {
					ParticlePack.this.run("tile", complete, loc);
				}
			});
	}
	
	public void run(String parent, CompletePortal complete, Location loc) {
		
		HashMap<String, Double> tempVars = new HashMap<String, Double>();

		tempVars.put("locX", loc.getX());
		tempVars.put("locY", loc.getY());
		tempVars.put("locZ", loc.getZ());
		tempVars.put("portalHeight", (double) complete.getPortalGeometry().getPortalHeight());
		tempVars.put("portalWidth", (double) complete.getPortalGeometry().getPortalWidth());
		
		for (String st : config.getStringList(parent+".play")) {
			
			playLine(st, loc, tempVars);
		}
		
		playLoop(loc, parent+".for", tempVars);
	}
	
	private void playLine(String st, Location loc, HashMap<String, Double> tempVars) {
		if (st.startsWith("debug")) {
			DimensionsDebbuger.debug(replaceVars(st.replaceFirst("debug ", ""), tempVars), DimensionsDebbuger.HIGH);
		} else if (st.startsWith("play")) {
			playParticle(loc, st, tempVars);
		} else if (st.contains("=")) {
			String[] var = st.split(" = ");
			tempVars.put(var[0], eval(var[1], tempVars));
		}

	}

	private void playLoop(Location loc, String loop, HashMap<String, Double> tempVars) {

		List<String> list = config.getStringList(loop);
		if (!list.isEmpty()) {
			for (String st : list) {
				playLine(st, loc, tempVars);
			}
		} else {
			if (config.getConfigurationSection(loop)==null) return;
			
			for(String key : config.getConfigurationSection(loop).getKeys(false)){
				
				if (key.contains(";")) {
					String[] sp = key.split(";");
					String[] a = sp[0].split("=");
					String[] b = sp[2].split("=");
					tempVars.put(a[0], eval(a[1], tempVars));
					
					while (equals(sp[1], tempVars)) {
						for(String key2 : config.getConfigurationSection(loop+"."+key).getKeys(false)){
							playLoop(loc, loop+"."+key+"."+key2, tempVars);
							playLoop(loc, loop+"."+key, tempVars);
						}
						

						tempVars.put(b[0], eval(b[1], tempVars));
					}

					tempVars.remove(a[0]);
				}
			}
		}
	}

	 private void playParticle(Location loc, String st, HashMap<String, Double> tempVars) {
		String[] spl = st.split(" ");
		if (st.contains("DustOptions")) {
			
			 String[] particlesColorString = spl[9].replace("DustOptions(Color(", "").replace(")", "").replaceAll(" ", "").split(",");
				Color color = Color.fromBGR(Integer.parseInt(particlesColorString[2]), Integer.parseInt(particlesColorString[1]), Integer.parseInt(particlesColorString[0]));
				loc.getWorld().spawnParticle(Particle.valueOf(spl[1]), eval(spl[2], tempVars), eval(spl[3], tempVars), eval(spl[4], tempVars), (int) eval(spl[5], tempVars), eval(spl[6], tempVars), eval(spl[7], tempVars), eval(spl[8], tempVars), new Particle.DustOptions(color,2));	
			} else {
				loc.getWorld().spawnParticle(Particle.valueOf(spl[1]), eval(spl[2], tempVars), eval(spl[3], tempVars), eval(spl[4], tempVars), (int) eval(spl[5], tempVars), eval(spl[6], tempVars), eval(spl[7], tempVars), eval(spl[8], tempVars));	
			}
		
	}

	private boolean equals(String cond, HashMap<String, Double> tempVars) {
		 if (cond.equals("true")) return true;
			
			if (cond.contains("==")) {
				String[] spl = cond.split("==");

				if (eval(spl[0], tempVars)==eval(spl[1], tempVars) && eval(spl[0], tempVars)!=Double.MAX_VALUE) return true;
				if (spl[0].equals(spl[1])) return true;
			} else if (cond.contains("!=")) {
				String[] spl = cond.split("!=");


				if (eval(spl[0], tempVars)!=eval(spl[1], tempVars) && eval(spl[0], tempVars)!=Double.MAX_VALUE) return true;
				if (!spl[0].equals(spl[1])) return true;
			} else if (cond.contains("<=")) {
				String[] spl = cond.split("<=");
					
				if (eval(spl[0], tempVars)<=eval(spl[1], tempVars)) return true;
			} else if (cond.contains("<")) {
				String[] spl = cond.split("<");
				if (eval(spl[0], tempVars)<eval(spl[1], tempVars)) return true;
			} else if (cond.contains(">=")) {
				String[] spl = cond.split(">=");
					
				if (eval(spl[0], tempVars)>=eval(spl[1], tempVars)) return true;
			} else if (cond.contains(">")) {
				String[] spl = cond.split(">");

				if (eval(spl[0], tempVars)>eval(spl[1], tempVars)) return true;
			}
			return false;
	 }
	
}
