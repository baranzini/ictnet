package org.cytoscape.ictnet2.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.ictnet2.internal.model.Disease;
import org.cytoscape.ictnet2.internal.model.Drug;
import org.cytoscape.ictnet2.internal.model.DrugGeneAssociation;
import org.cytoscape.ictnet2.internal.model.GWASAssociation;
import org.cytoscape.ictnet2.internal.model.Gene;
import org.cytoscape.ictnet2.internal.model.Molecule;
import org.cytoscape.ictnet2.internal.model.PPI;
import org.cytoscape.ictnet2.internal.model.TissueGeneAssociation;

public class DBQueryLibrary {
	public static HashMap<Integer, Set<GWASAssociation>> dbQueryDiseaseGeneGWAS(Connection connect, Set<Integer> dIds, int gwasId){
		HashMap<Integer, Set<GWASAssociation>> result = new HashMap<Integer, Set<GWASAssociation>>();
		String sql = "{call " + DBConstants.SP_DISEASE_GENE_EFO + "(?,?)}";	
		List<String> qStrs = convertIntegerSetToSQLString(dIds);	
		for(String qStr: qStrs){			
			try{			
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				cs.setString(2, Integer.toString(gwasId));
				ResultSet rs = cs.executeQuery();
				while(rs.next()){	
					Integer did = rs.getInt("e.doid_id");
					Integer gid = rs.getInt("e.gene_id");							
					Integer confidence = rs.getInt(3);
					Integer primary = rs.getInt(4);	
					String pubmeds = rs.getString(5);					
				
					GWASAssociation newAssociation = new GWASAssociation(did, gid, confidence, primary, pubmeds);
					if (result.containsKey(did)){					  
						result.get(did).add(newAssociation);				  
					}else{
						Set<GWASAssociation> newSet = new HashSet<GWASAssociation>();
						result.put(did, newSet);
					}//if-else
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//
		}//for				
		return result;
	}//
	
	public static HashMap<Integer, Set<GWASAssociation>> dbQueryGeneDiseaseGWAS(Connection connect, Set<Integer> gIDs, int gwasId){
		HashMap<Integer, Set<GWASAssociation>> result = new HashMap<Integer, Set<GWASAssociation>>();
		String sql = "{call " + DBConstants.SP_GENE_DISEASE_EFO + "(?,?)}";	
        List<String> qStrs = convertIntegerSetToSQLString(gIDs);	
		for(String qStr: qStrs){		
		    try{			
		    	CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				cs.setString(2, Integer.toString(gwasId));
				ResultSet rs = cs.executeQuery();
				while(rs.next()){				
					Integer gid = rs.getInt("e.gene_id");
					Integer did = rs.getInt("e.doid_id");					
					Integer primary = rs.getInt(4);				
					Integer confidence = rs.getInt(3);	
					String pubmeds = rs.getString(5);	
				
					GWASAssociation newAssociation = new GWASAssociation(did, gid, confidence, primary, pubmeds);
					if (result.containsKey(did)){
						result.get(did).add(newAssociation);						
					}else{						
						Set<GWASAssociation> newSet = new HashSet<GWASAssociation>();
						newSet.add(newAssociation);
						result.put(did, newSet);
					}//if-else
					
				}//while
				rs.close();
				cs.close();
		    }catch(SQLException sqlex){
			    System.out.println(sqlex.toString());	
		    }catch(Exception ex){
			    System.out.println(ex.toString());
		    }//	
		}//for
		return result;
	}//
	
	public static HashMap<Integer, Set<GWASAssociation>> dbQueryDiseaseGeneEdgeGWAS(Connection connect, Set<Integer> dIds,  Set<Integer> gIds, int gwasId){
		HashMap<Integer, Set<GWASAssociation>> result = new HashMap<Integer, Set<GWASAssociation>>();
		String sql = "{call " + DBConstants.SP_GENE_DISEASE_EFO_EDGE + "(?,?,?)}";	
		List<String> qStrs = convertIntegerSetToSQLString(dIds);	
		List<String> gStrs = convertIntegerSetToSQLString(gIds);
		for(String gStr: gStrs){
			for(String qStr: qStrs){			
				try{			
					CallableStatement cs = connect.prepareCall(sql);
					cs.setString(1, qStr);
					cs.setString(2, gStr);
					cs.setString(3, Integer.toString(gwasId));
					ResultSet rs = cs.executeQuery();
					while(rs.next()){	
						Integer did = rs.getInt("e.doid_id");
						Integer gid = rs.getInt("e.gene_id");				
						Integer primary = rs.getInt(4);				
						Integer confidence = rs.getInt(3);	
						String pubmeds = rs.getString(5);
					
						GWASAssociation newAssociation = new GWASAssociation(did, gid, confidence, primary, pubmeds);
						if (result.containsKey(did)){					  
							result.get(did).add(newAssociation);				  
						}else{
							Set<GWASAssociation> newSet = new HashSet<GWASAssociation>();
							result.put(did, newSet);
						}//if-else
					}//while
					rs.close();
					cs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//
			}//for	
		}//for
					
		return result;
	}//
	
	public static HashMap<Integer, Set<Integer>> dbQueryGeneDiseaseOMIM(Connection connect, Set<Integer> gIDs){
		HashMap<Integer, Set<Integer>> result = new HashMap<Integer, Set<Integer>>();		
		String sql1 ="{call " + DBConstants.SP_GENE_DISEASE_OMIM + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIDs);	
		for(String qStr: qStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql1);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();
							
				while(rs.next()){				
					Integer gid = rs.getInt("e.gene_id");	
					Integer did = rs.getInt("m.doid_id");
					if (!result.containsKey(did)){
						Set<Integer> newSet = new HashSet<Integer>();
					    newSet.add(gid);
					    result.put(did, newSet);
					}else{
						result.get(did).add(gid);
					}//if-else
					
				}//while
				rs.close();
				cs.close();
		}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
		}catch(Exception ex){
				System.out.println(ex.toString());
		}//	
		}//for
				
		return result;		
	}//
	
	public static HashMap<Integer, Set<Integer>> dbQueryDiseaseGeneOMIM(Connection connect, Set<Integer> dIds){
		HashMap<Integer, Set<Integer>> result = new HashMap<Integer, Set<Integer>>();		
		String sql1 ="{call " + DBConstants.SP_DISEASE_GENE_OMIM + "(?)}";	
		List<String> qStrs = convertIntegerSetToSQLString(dIds);	
		for(String qStr: qStrs){	
			try{	
				CallableStatement cs = connect.prepareCall(sql1);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();
							
				while(rs.next()){
					Integer did = rs.getInt("m.doid_id");
					Integer gid = rs.getInt("e.gene_id");											
					if (result.containsKey(did)){
						result.get(did).add(gid);
					}else{
						Set<Integer> newSet = new HashSet<Integer>();
						newSet.add(gid);
						result.put(did, newSet);
					}//if-else					   
				}//while
				rs.close();
				cs.close();
		    }catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
		    }catch(Exception ex){
				System.out.println(ex.toString());
		    }//try-catch	
		}//for				
		return result;		
	}//
	
	public static HashMap<Integer, Set<Integer>> dbQueryDiseaseGeneEdgeOMIM(Connection connect, Set<Integer> dIds, Set<Integer> gIds){
		HashMap<Integer, Set<Integer>> result = new HashMap<Integer, Set<Integer>>();		
		String sql1 ="{call " + DBConstants.SP_GENE_DISEASE_OMIM_EDGE + "(?,?)}";	
		List<String> qStrs = convertIntegerSetToSQLString(dIds);
		List<String> gStrs = convertIntegerSetToSQLString(gIds);
		for(String gStr: gStrs){
			for(String qStr: qStrs){	
				try{	
					CallableStatement cs = connect.prepareCall(sql1);
					cs.setString(1, qStr);
					cs.setString(2, gStr);
					ResultSet rs = cs.executeQuery();
								
					while(rs.next()){
						Integer did = rs.getInt("m.doid_id");
						Integer gid = rs.getInt("e.gene_id");											
						if (result.containsKey(did)){
							result.get(did).add(gid);
						}else{
							Set<Integer> newSet = new HashSet<Integer>();
							newSet.add(gid);
							result.put(did, newSet);
						}//if-else					   
					}//while
					rs.close();
					cs.close();
			    }catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
			    }catch(Exception ex){
					System.out.println(ex.toString());
			    }//try-catch	
			}//for	
		}//for
					
		return result;		
	}//
	
	public static HashMap<Integer, Set<Integer>> dbQueryGeneDiseaseMedic(Connection connect, Set<Integer> gIDs){
		HashMap<Integer, Set<Integer>> result = new HashMap<Integer, Set<Integer>>();		
		String sql2 = "{call " + DBConstants.SP_GENE_DISEASE_MEDIC + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIDs);	
		for(String qStr: qStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql2);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();				
								
				while(rs.next()){					
					Integer gid = rs.getInt("e.gene_id");
					Integer did = rs.getInt("m.doid_id");
					if (result.containsKey(gid)){
						result.get(did).add(gid);
					}else{
						Set<Integer> newSet = new HashSet<Integer>();
						newSet.add(gid);
						result.put(did, newSet);						
					}//if-else											 
				}//while
				rs.close();
				cs.close();
		    }catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
		    }catch(Exception ex){
				System.out.println(ex.toString());
		    }//
		}//for				
	    return result;		
	}
	
	public static HashMap<Integer, Set<Integer>> dbQueryDiseaseGeneMedic(Connection connect,  Set<Integer> dIds){
		HashMap<Integer, Set<Integer>> result = new HashMap<Integer, Set<Integer>>();		
		String sql2 = "{call " + DBConstants.SP_DISEASE_GENE_MEDIC + "(?)}";	
		List<String> qStrs = convertIntegerSetToSQLString(dIds);	
		for(String qStr: qStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql2);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();				
								
				while(rs.next()){	
					Integer did = rs.getInt("m.doid_id");
					Integer gid = rs.getInt("e.gene_id");										
					if (result.containsKey(did)){
						result.get(did).add(gid);
					}else{
						Set<Integer> newSet = new HashSet<Integer>();
						newSet.add(gid);
						result.put(did, newSet);
					}//if-else
				}//while
				rs.close();
				cs.close();
		}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
		}catch(Exception ex){
				System.out.println(ex.toString());
		}//
		}//for			
	    return result;		
	}
	
	public static HashMap<Integer, Set<Integer>> dbQueryDiseaseGeneEdgeMedic(Connection connect,  Set<Integer> dIds, Set<Integer> gIds){
		HashMap<Integer, Set<Integer>> result = new HashMap<Integer, Set<Integer>>();		
		String sql2 = "{call " + DBConstants.SP_GENE_DISEASE_MEDIC_EDGE + "(?,?)}";	
		List<String> qStrs = convertIntegerSetToSQLString(dIds);
		List<String> gStrs = convertIntegerSetToSQLString(gIds);
		for(String gStr: gStrs){
			for(String qStr: qStrs){
				try{	
					CallableStatement cs = connect.prepareCall(sql2);
					cs.setString(1, qStr);
					cs.setString(2, gStr);
					ResultSet rs = cs.executeQuery();				
									
					while(rs.next()){	
						Integer did = rs.getInt("m.doid_id");
						Integer gid = rs.getInt("e.gene_id");										
						if (result.containsKey(did)){
							result.get(did).add(gid);
						}else{
							Set<Integer> newSet = new HashSet<Integer>();
							newSet.add(gid);
							result.put(did, newSet);
						}//if-else
					}//while
					rs.close();
					cs.close();
			    }catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
			    }catch(Exception ex){
					System.out.println(ex.toString());
			    }//
			}//for
		}//for
					
	    return result;		
	}
	
	public static HashMap<String, Drug> dbQueryGeneDrugCTD(Connection connect, Set<Integer> gIds){
		HashMap<String, Drug> result = new HashMap<String, Drug> ();		
		String sql = "{call " + DBConstants.SP_GENE_DRUG_CTD + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIds);	
		for(String qStr: qStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();							
				while(rs.next()){
					String meshID = rs.getString("g.mesh_id");
					String dName = rs.getString("d.name");					
					Drug newDrug = new Drug(meshID, dName);
					if(!result.containsKey(meshID)) {
						result.put(meshID, newDrug);
					}//if
				}//while
				rs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for
		return result;
	}
	
	public static HashMap<String, Drug> dbQueryGeneDrugDrugbank(Connection connect, Set<Integer> gIds){
		HashMap<String, Drug> result = new HashMap<String, Drug> ();		
		String sql = "{call " + DBConstants.SP_GENE_DRUG_DRUGBANK + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIds);	
		for(String qStr: qStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();							
				while(rs.next()){
					String meshID = rs.getString("g.mesh_id");
					String dName = rs.getString("d.name");
					int drugbankID = rs.getInt(3);
					Drug newDrug = new Drug(drugbankID, meshID, dName);
					if(!result.containsKey(meshID)) {
						result.put(meshID, newDrug);
					}//if
				}//while
				rs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for
		return result;
	}
	public static HashMap<Integer, Set<Drug>> dbQueryDiseaseDrugCTD(Connection connect, Set<Integer> dIds){
		HashMap<Integer, Set<Drug>> result = new HashMap<Integer, Set<Drug>>();		
		String sql = "{call " + DBConstants.SP_DISEASE_DRUG_CTD + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(dIds);	
		for(String qStr: qStrs){
			try{		
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();							
				while(rs.next()){			
					int disId = rs.getInt("m.doid_id");
					String did = rs.getString("d.mesh_id");				
					String dName = rs.getString("d.name");
					int drugbankID = rs.getInt("c.drugbank_id");
					Drug newDrug = new Drug(drugbankID, did, dName);
					if (result.containsKey(disId)){
						result.get(disId).add(newDrug);
					}else{
						Set<Drug> newSet = new HashSet<Drug>();
						newSet.add(newDrug);
						result.put(disId, newSet);
					}//if-else
					
				}//while
				rs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for		
		return result;
	}
	public static HashMap<Integer, Set<String>> dbQueryDrugDiseaseCTD(Connection connect, Set<String> drugIds){
		HashMap<Integer, Set<String>> result = new HashMap<Integer, Set<String>>();		
		String sql = "{call " + DBConstants.SP_DRUG_DISEASE_CTD + "(?)}";
		List<String> qStrs = convertStringSetToSQLString(drugIds);	
		for(String qStr: qStrs){
			try{		
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();							
				while(rs.next()){			
					int disId = rs.getInt("m.doid_id");
					String drugId = rs.getString("t.mesh_id");				
					if(result.containsKey(disId)){
						result.get(disId).add(drugId);
					}else{
						Set<String> newSet = new HashSet<String>();
						newSet.add(drugId);
						result.put(disId, newSet);
					}//if-else
					
				}//while
				rs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for		
		return result;
	}
	
	public static HashMap<Integer, Set<String>> dbQueryDiseaseDrugEdgeCTD(Connection connect, Set<Integer> disIds, Set<String> drugIds){
		HashMap<Integer, Set<String>> result = new HashMap<Integer, Set<String>>();		
		String sql = "{call " + DBConstants.SP_DISEASE_DRUG_CTD_EDGE + "(?, ?)}";
		List<String> qStrs = convertIntegerSetToSQLString(disIds);
		List<String> dStrs = convertStringSetToSQLString(drugIds);
		for(String dStr: dStrs){
			for(String qStr: qStrs){
				try{		
					CallableStatement cs = connect.prepareCall(sql);
					cs.setString(1, dStr);
					cs.setString(2, qStr);
					ResultSet rs = cs.executeQuery();							
					while(rs.next()){			
						int disId = rs.getInt("m.doid_id");
						String drgId = rs.getString("t.mesh_id");					
						if (result.containsKey(disId)){
							result.get(disId).add(drgId);
						}else{
							Set<String> newSet = new HashSet<String>();
							newSet.add(drgId);
							result.put(disId, newSet);
						}//if-else
						
					}//while
					rs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//	
			}//for	
		}//for			
		return result;
	}
	public static HashMap<String, HashMap<String, String>> dbQuerySideEffect(Connection connect, Set<String> meshIDs, 
			String sideEffectStr, double sideEffectFreq, boolean uncat){
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();		
		String sql = "{call " + DBConstants.SP_SIDE_EFFECT + "(?, ?, ?, ?)}";
		List<String> mStrs = convertStringSetToSQLString(meshIDs);
		//System.out.println("iCTNet App: Querying side effect data");
		for(String mStr: mStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, mStr);
				cs.setString(2, sideEffectStr);
				cs.setDouble(3, sideEffectFreq);
				cs.setBoolean(4,  uncat);
				ResultSet rs = cs.executeQuery();					
				while(rs.next()){	
					String meshID = rs.getString(1);
					String sid = rs.getString(3);
					String sName = rs.getString(2);
					if(result.containsKey(meshID)){
						result.get(meshID).put(sid, sName);
					}else{
						HashMap<String, String> newMap = new HashMap<String, String>();
						newMap.put(sid, sName);
						result.put(meshID, newMap);
					}//if-else
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for		
		return result;
	}//
	public static HashMap<Integer, HashMap<String, String>> dbQueryDiseaseTissue(Connection connect, Set<Integer> dIds){
		HashMap<Integer, HashMap<String, String>> result = new HashMap<Integer, HashMap<String, String>>();		
		String sql = "{call " + DBConstants.SP_DISEASE_TISSUE + "(?)}";
		List<String> dStrs = convertIntegerSetToSQLString(dIds);
		for(String dStr: dStrs){
			try{		
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, dStr);
				ResultSet rs = cs.executeQuery();	
							
				while(rs.next()){	
					int did = rs.getInt("m.doid_id");
					String tid = rs.getString("t.bto_id");
					String tName = rs.getString("t.name");
					if(result.containsKey(did)){
						result.get(did).put(tid, tName);
					}else{
						HashMap<String, String> newMap = new HashMap<String, String>();
						newMap.put(tid, tName);
						result.put(did, newMap);
					}//if-else
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for
    	return result;
	}//
	
	public static HashMap<Integer, Set<String>> dbQueryDiseaseTissueEdge(Connection connect, Set<Integer> dIDs, Set<String> tIDs){
		HashMap<Integer, Set<String>> result = new HashMap<Integer, Set<String>>();		
		String sql = "{call " + DBConstants.SP_DISEASE_TISSUE_EDGE + "(?,?)}";
		List<String> dStrs = convertIntegerSetToSQLString(dIDs);
		List<String> tStrs = convertStringSetToSQLString(tIDs);
		
		for(String dStr: dStrs){
			for(String tStr: tStrs){
				try{		
					CallableStatement cs = connect.prepareCall(sql);
					cs.setString(1, dStr);
					cs.setString(2, tStr);
					ResultSet rs = cs.executeQuery();	
								
					while(rs.next()){	
						//t.name, t.bto_id
						Integer did = rs.getInt("m.doid_id");
						String tid = rs.getString("m.bto_id");
						if (result.containsKey(did)){
							result.get(did).add(tid);
						}else{
							Set<String> newSet = new HashSet<String>();
							newSet.add(tid);
							result.put(did, newSet);
						}//if-else						
					}//while
					rs.close();
					cs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//	
			}//for
		}//for		
		return result;
	}//
	
	public static HashMap<String, Set<String>> dbQuerySideEffectTissueEdge(Connection connect, Set<String> sIDs, Set<String> tIDs){
		HashMap<String, Set<String>> result = new HashMap<String, Set<String>>();		
		String sql = "{call " + DBConstants.SP_TISSUE_SIDE_EFFECT_EDGE + "(?,?)}";
		List<String> sStrs = convertStringSetToSQLString(sIDs);
		List<String> tStrs = convertStringSetToSQLString(tIDs);
		
		for(String sStr: sStrs){
			for(String tStr: tStrs){
				try{		
					CallableStatement cs = connect.prepareCall(sql);
					cs.setString(1, sStr);
					cs.setString(2, tStr);
					ResultSet rs = cs.executeQuery();	
								
					while(rs.next()){	
						//t.name, t.bto_id
						String sid = rs.getString("s.umls_id");
						String tid = rs.getString("s.bto_id");
						if (result.containsKey(sid)){
							result.get(sid).add(tid);
						}else{
							Set<String> newSet = new HashSet<String>();
							newSet.add(tid);
							result.put(sid, newSet);
						}//if-else						
					}//while
					rs.close();
					cs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//	
			}//for
		}//for		
		return result;
	}//
	
	public static Set<TissueGeneAssociation> dbQueryGeneTissue(Connection connect, Set<Integer> gIDs, HashMap<String, String> tissues){
		Set<TissueGeneAssociation> result = new HashSet<TissueGeneAssociation>();
		String sql = "{call " + DBConstants.SP_GENE_TISSUE + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIDs);
		
		System.out.println("iCTNet App: Querying tissue data");
		for(String qStr: qStrs){
			try{		
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();	
							
				while(rs.next()){	
					int gid = rs.getInt("g.gene_id");					
					String tid = rs.getString("t.bto_id");	
					String tName = rs.getString("t.name");
					double value = rs.getDouble("g.log_expr");
					if (!tissues.containsKey(tid))
					    tissues.put(tid, tName);
					TissueGeneAssociation newtgCon = new TissueGeneAssociation(tid, gid, value);
					result.add(newtgCon);
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//			
		}//for		
		return result;
	}//
	
	public static HashSet<Integer> dbQueryPPI(Connection connect, Integer gID){
		HashSet<Integer> result = new HashSet<Integer>();
		String sql = "{call " + DBConstants.SP_PPI + "(?)}";
		
		System.out.println("iCTNet App: Querying PPI data");
		try{	
			CallableStatement cs = connect.prepareCall(sql);
			cs.setInt(1, gID);
			ResultSet rs = cs.executeQuery();	
						
			while(rs.next()){				
				Integer gid = rs.getInt("g.gene_id");
				if (!result.contains(gid))
				    result.add(gid);
			}//while
			rs.close();
			cs.close();
		}catch(SQLException sqlex){
			System.out.println(sqlex.toString());	
		}catch(Exception ex){
			System.out.println(ex.toString());
		}//	
		return result;
	}//
	
	public static Set<PPI> dbQueryPPINewNeighborBatch(Connection connect, Set<Integer> gIDs){
		Set<PPI> result = new HashSet<PPI>();
		String sql = "{call " + DBConstants.SP_PPI_NEW_NEIGHBOR_BATCH + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIDs);
		
		System.out.println("iCTNet App: Querying PPI data for nodes");
		for(String qStr: qStrs){			
			try{			
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();	
							
				while(rs.next()){
					//p.source, p.target 
					Integer gSource= rs.getInt("p.source");
					Integer gTarget= rs.getInt("p.target");
					String pubmeds = rs.getString(3);
					PPI newPPI = new PPI(gSource, gTarget, pubmeds);
					result.add(newPPI);
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for		
		return result;
	}//
	
	public static Set<PPI> dbQueryPPIEdgeBatch(Connection connect, Set<Integer> gIDs){
		Set<PPI> result = new HashSet<PPI>();
		String sql = "{call " + DBConstants.SP_PPI_EDGE_BATCH + "(?,?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIDs);		
		System.out.println("iCTNet App: Querying PPI data for edges");
		for(String qStr1: qStrs){
			for(String qStr2: qStrs){
				try{			
					CallableStatement cs = connect.prepareCall(sql);
					//System.out.println("query string 1: "+ qStr1);
					//System.out.println("query string 2: "+ qStr2);
					cs.setString(1, qStr1);
					cs.setString(2, qStr2);
					ResultSet rs = cs.executeQuery();			
					while(rs.next()){
						//p.source, p.target 
						Integer gSource= rs.getInt("p.source");
						Integer gTarget= rs.getInt("p.target");
						String pubmeds = rs.getString(3);
						PPI newPPI = new PPI(gSource, gTarget, pubmeds);
						result.add(newPPI);
					}//while
					rs.close();
					cs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//	
			}//for
		}//for				
		return result;
	}//	
	
	public static void dbQueryGeneAttrs(Connection connect, HashMap<Integer,Gene> gMap){		
		String sql = "{call " + DBConstants.SP_GENE_ATTIBUTES + "(?)}";		
		List<String> qStrs = convertIntegerSetToSQLString(gMap.keySet());
				
		System.out.println("iCTNet App: Querying gene data");
		for(String qStr: qStrs){
			try{			
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);
				ResultSet rs = cs.executeQuery();			
				while(rs.next()){	
					//g.symbol, g.gene_id, g.name, g.location 
					Integer gid = rs.getInt("g.gene_id");					
					String symbol = rs.getString("g.symbol");				
					String description = rs.getString("g.name");
					String location = rs.getString("g.location");
					Gene gene = gMap.get(gid);
					gene.setName(symbol);
					gene.setGeneDescription(description);
					gene.setGeneLocation(location);		
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//		
		}//for			
	}//	
	
	public static Set<TissueGeneAssociation> dbQueryTissueGeneAssociation(Connection connect, Set<String> tisIDs, Set<Integer> gIDs){
		Set<TissueGeneAssociation> result = new HashSet<TissueGeneAssociation>();
		String sql = "{call " + DBConstants.SP_TISSUE_GENE_EDGE + "(?, ?)}";
		List<String> tisStrs = convertStringSetToSQLString(tisIDs);
		List<String> gStrs = convertIntegerSetToSQLString(gIDs);
		
		System.out.println("iCTNet App: Querying Tissue-Gene associations");
		for(String tisStr: tisStrs){
			for(String gStr: gStrs){
				try{
					CallableStatement cs = connect.prepareCall(sql);
					cs.setString(1, tisStr);
					cs.setString(2, gStr);
					ResultSet rs = cs.executeQuery();
								
					while(rs.next()){
						//g.gene_id, g.bto_id, g.log_expr
						Integer gid= rs.getInt("g.gene_id");
						String tid= rs.getString("g.bto_id");
						double value = rs.getDouble("g.log_expr");
						TissueGeneAssociation newtgCon = new TissueGeneAssociation(tid, gid, value);
						result.add(newtgCon);
					}//while
					rs.close();
					cs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//	
			}//for
		}//for				
		return result;
	}//
	
	public static Set<Integer> dbQueryDrugGeneCTD(Connection connect, Set<String> meshIDs){
		Set<Integer> result = new HashSet<Integer>();
		String sql = "{call " + DBConstants.SP_DRUG_GENE_CTD + "(?)}";
		List<String> mStrs = convertStringSetToSQLString(meshIDs);
		for(String mStr: mStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, mStr);				
				ResultSet rs = cs.executeQuery();
							
				while(rs.next()){						
					Integer gid= rs.getInt("g.gene_id");					
					result.add(gid);					
				}//while
				rs.close();
			}catch(SQLException sqlex){
				System.out.println("Query drug-gene in CTD: "+sqlex.toString());	
			}catch(Exception ex){
				System.out.println("Query drug-gene in CTD: "+ex.toString());
			}//	
		}//for
		return result;
	}//
	public static Set<Integer> dbQueryDrugGeneDrugbank(Connection connect, Set<String> meshIDs, Set<Integer> drugbankIDs){
		Set<Integer> result = new HashSet<Integer>();
		String sql = "{call " + DBConstants.SP_DRUG_GENE_DRUGBANK + "(?)}";
		List<String> mStrs = convertStringSetToSQLString(meshIDs);
		for(String mStr: mStrs){
			try{	
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, mStr);				
				ResultSet rs = cs.executeQuery();
							
				while(rs.next()){						
					Integer gid= rs.getInt("g.gene_id");
					Integer drugbank_id = rs.getInt(2);
					result.add(gid);
					if (drugbank_id != -1){
						drugbankIDs.add(drugbank_id);
					}//if
				}//while
				rs.close();
			}catch(SQLException sqlex){
				System.out.println("Query drug-gene in drugbank: "+sqlex.toString());	
			}catch(Exception ex){
				System.out.println("Query drug-gene in drugbank: "+ex.toString());
			}//	
		}//for
		return result;
	}//
	
	public static HashMap<Integer, HashMap<String, DrugGeneAssociation>> dbQueryDrugGeneEdgeDrugBank(Connection connect, Set<Integer> drugbankIDs, Set<Integer> gIDs){
		HashMap<Integer, HashMap<String, DrugGeneAssociation>> result = new HashMap<Integer, HashMap<String, DrugGeneAssociation>>();
		String sql = "{call " + DBConstants.SP_DRUG_GENE_DRUGBANK_EDGE + "(?, ?)}";
		List<String> drugbankStrs = convertIntegerSetToSQLString(drugbankIDs);
		List<String> gStrs = convertIntegerSetToSQLString(gIDs);
		
		for(String drugbankStr: drugbankStrs){
			for(String gStr: gStrs){
				System.out.println("iCTNet App: Querying Drug-Gene drugBank associations");
				try{	
					CallableStatement cs = connect.prepareCall(sql);
					cs.setString(1, drugbankStr);
					cs.setString(2, gStr);
					ResultSet rs = cs.executeQuery();
								
					while(rs.next()){						
						Integer gid= rs.getInt("g.gene_id");
						String meshID= rs.getString("m.mesh_id");
						int value = rs.getInt("g.pharmacological");
						String action = rs.getString("g.actions");
						DrugGeneAssociation newObject = new DrugGeneAssociation(meshID, gid, value, action);
						if(result.containsKey(gid)){
							result.get(gid).put(meshID, newObject);
						}else{
							HashMap<String, DrugGeneAssociation> newMap = new HashMap<String, DrugGeneAssociation>();
							newMap.put(meshID, newObject);
							result.put(gid, newMap);
						}//if-else
					}//while
					rs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//	
			}//for
		}//for		
		return result;
	}//
	
	public static HashMap<Integer, HashMap<String, DrugGeneAssociation>> dbQueryDrugGeneEdgeCTD(Connection connect, Set<String> meshIDs, Set<Integer> gIDs){
		HashMap<Integer, HashMap<String, DrugGeneAssociation>>  result = new HashMap<Integer, HashMap<String, DrugGeneAssociation>>();
		String sql = "{call " + DBConstants.SP_DRUG_GENE_CTD_EDGE + "(?, ?)}";
		List<String> meshStrs = convertStringSetToSQLString(meshIDs); 
		List<String> gStrs = convertIntegerSetToSQLString(gIDs);
		
		System.out.println("iCTNet App: Querying Drug-Gene CTD associations");
		for(String meshStr: meshStrs){
			for(String gStr: gStrs){
				try{		
					CallableStatement cs = connect.prepareCall(sql);
					cs.setString(1, meshStr);
					cs.setString(2, gStr);
					ResultSet rs = cs.executeQuery();
								
					while(rs.next()){
						//g.gene_id, g.mesh_id
						Integer gid= rs.getInt("g.gene_id");
						String meshid= rs.getString("g.mesh_id");
						String pubmeds = rs.getString("g.pubmeds");						
						DrugGeneAssociation newObject = new DrugGeneAssociation(meshid, gid, pubmeds);
						if(result.containsKey(gid)){
							result.get(gid).put(meshid, newObject);
						}else{
							HashMap<String, DrugGeneAssociation> newMap = new HashMap<String, DrugGeneAssociation>();
							newMap.put(meshid, newObject);
							result.put(gid, newMap);
						}//if-else
					}//while
					rs.close();
					cs.close();
				}catch(SQLException sqlex){
					System.out.println(sqlex.toString());	
				}catch(Exception ex){
					System.out.println(ex.toString());
				}//	
			}//for
		}//for				
		return result;
	}//
	
	public static Set<PPI> dbQueryMiRNA(Connection connect, Set<Integer> gIDs){
		Set<PPI> result = new HashSet<PPI>();
		String sql = "{call " + DBConstants.SP_MIRNA + "(?)}";
		List<String> qStrs = convertIntegerSetToSQLString(gIDs);
		
		System.out.println("iCTNet App: Querying miRNA data");
		for(String qStr: qStrs){
			try{
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);			
				ResultSet rs = cs.executeQuery();
						
				while(rs.next()){
					//p.source_gene_id, p.target_gene_id
					Integer gSource= rs.getInt("p.source_gene_id");
					Integer gTarget= rs.getInt("p.target_gene_id");
					String pubmeds = rs.getString("p.pubmed");
					PPI newPPI = new PPI(gSource, gTarget, pubmeds);
					result.add(newPPI);
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//
		}//for			
		return result;		
	}//
	
	public static List<Molecule> dbBatchQueryDiseaseName(Connection connect, Set<String> querySet){
		List<Molecule> result = new ArrayList<Molecule>();
		String sql = "{call " + DBConstants.SP_DISEASE_ONTOLOGY_BATCH + "(?)}";		
		List<String> qStrs = convertStringSetToSQLString(querySet);	
		
		for(String qStr: qStrs){
			try{
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);			
				ResultSet rs = cs.executeQuery();		
				while(rs.next()){				
					String dName = rs.getString("d.name");				
					Integer id = rs.getInt("d.doid_id");
					//System.out.println(id);
					Disease newDisease = new Disease(id, dName);
					result.add(newDisease);
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//		
		}//for			
		return result;
	}
	public static List<Molecule> dbQueryDiseaseName(Connection connect, String queryStr){
		List<Molecule> result = new ArrayList<Molecule>();
		String sql = "{call " + DBConstants.SP_DISEASE_ONTOLOGY + "(?)}";		
		String qStr = DBConstants.sqlQueryStringCheck(queryStr) + '%';		
		try{
			CallableStatement cs = connect.prepareCall(sql);
			cs.setString(1, qStr);			
			ResultSet rs = cs.executeQuery();		
			while(rs.next()){				
				String dName = rs.getString("d.name");				
				Integer id = rs.getInt("d.doid_id");
				//System.out.println(id);
				Disease newDisease = new Disease(id, dName);
				result.add(newDisease);
			}//while
			rs.close();
			cs.close();
		}catch(SQLException sqlex){
			System.out.println(sqlex.toString());	
		}catch(Exception ex){
			System.out.println(ex.toString());
		}//			
		return result;
	}//	
	
	public static List<Molecule> dbBatchQueryGeneName(Connection connect, Set<String> querySet){
		List<Molecule> result = new ArrayList<Molecule>();
		String sql = "{call " + DBConstants.SP_GENE_QUERY_BATCH + "(?)}";	
		List<String> qStrs = convertStringSetToSQLString(querySet);
		for(String qStr: qStrs){
			try{
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);			
				ResultSet rs = cs.executeQuery();		
				while(rs.next()){				
					String gSymbol = rs.getString("g.symbol");				
					Integer id = rs.getInt("g.gene_id");
					//System.out.println(id);
					Gene newGene = new Gene(id);
					newGene.setName(gSymbol);
					result.add(newGene);
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//	
		}//for
		return result;
	}
	public static List<Molecule> dbQueryGeneName(Connection connect, String queryStr){
		List<Molecule> result = new ArrayList<Molecule>();
		String sql = "{call " + DBConstants.SP_GENE_QUERY + "(?)}";		
		String qStr = DBConstants.sqlQueryStringCheck(queryStr) + '%';		
		try{
			CallableStatement cs = connect.prepareCall(sql);			
			cs.setString(1, qStr);			
			ResultSet rs = cs.executeQuery();			
			while(rs.next()){				
				String gSymbol = rs.getString("g.symbol");				
				Integer id = rs.getInt("g.gene_id");
				//System.out.println(id);
				Gene newGene = new Gene(id);
				newGene.setName(gSymbol);
				result.add(newGene);
			}//while
			rs.close();
			cs.close();
		}catch(SQLException sqlex){
			System.out.println(sqlex.toString());	
		}catch(Exception ex){
			System.out.println(ex.toString());
		}//			
		return result;
	}//	
	public static List<Drug> dbBatchQueryDrugName(Connection connect, Set<String> querySet){
		List<Drug> result = new ArrayList<Drug>();
		String sql = "{call " + DBConstants.SP_DRUG_QUERY_BATCH + "(?)}";	
		List<String> qStrs = convertStringSetToSQLString(querySet);
		for(String qStr: qStrs){
			try{
				CallableStatement cs = connect.prepareCall(sql);
				cs.setString(1, qStr);			
				ResultSet rs = cs.executeQuery();		
				while(rs.next()){
					String id = rs.getString("d.mesh_id");
					String dname = rs.getString("d.name");				
					//System.out.println(id);
					Drug newDrug = new Drug(id, dname);				
					result.add(newDrug);
				}//while
				rs.close();
				cs.close();
			}catch(SQLException sqlex){
				System.out.println(sqlex.toString());	
			}catch(Exception ex){
				System.out.println(ex.toString());
			}//		
		}//for
		return result;
	}
	
	public static List<Drug> dbQueryDrugName(Connection connect, String queryStr){
		List<Drug> result = new ArrayList<Drug>();
		String sql = "{call " + DBConstants.SP_DRUG_QUERY + "(?)}";		
		String qStr = DBConstants.sqlQueryStringCheck(queryStr) + '%';		
		try{
			CallableStatement cs = connect.prepareCall(sql);
			cs.setString(1, qStr);			
			ResultSet rs = cs.executeQuery();		
			while(rs.next()){
				String id = rs.getString("d.mesh_id");
				String dname = rs.getString("d.name");				
				//System.out.println(id);
				Drug newDrug = new Drug(id, dname);				
				result.add(newDrug);
			}//while
			rs.close();
			cs.close();
		}catch(SQLException sqlex){
			System.out.println(sqlex.toString());	
		}catch(Exception ex){
			System.out.println(ex.toString());
		}//			
		return result;
	}//
	
	public static List<String> convertStringSetToSQLString(Set<String> idSet){
		List<String> qStrList = new ArrayList<String>();
		String[] strArray = idSet.toArray(new String[idSet.size()]);
		if (strArray.length < 1)
			return qStrList;
		StringBuilder qStr = new StringBuilder();		
		for(int i= 0; i<strArray.length-1; i++){
			qStr.append(strArray[i]);
			if (qStr.length() >= DBConstants.MAX_QUERY_LEN -250){//length for drug name = 250		    	
		    	qStrList.add(qStr.toString());		    	
		    	qStr = new StringBuilder();		    	
		    }else{
		    	qStr.append(",");
		    }//if-else
		}//for
		qStr.append(strArray[strArray.length-1]);		
		qStrList.add(qStr.toString());		
		return qStrList;			
	}//
	
	public static List<String> convertIntegerSetToSQLString(Set<Integer> idSet){
		List<String> qStrList = new ArrayList<String>();		
		Integer[] strArray = idSet.toArray(new Integer[idSet.size()]);
		if (strArray.length < 1)
			return qStrList;
		StringBuilder qStr = new StringBuilder();
		for(int i= 0; i<strArray.length-1; i++){
			qStr.append(strArray[i]);
		    if (qStr.length() >= DBConstants.MAX_QUERY_LEN -250){//length for drug name = 250		    	
		    	qStrList.add(qStr.toString());		    	
		    	qStr = new StringBuilder();		    	
		    }else{
		    	qStr.append(",");
		    }
		}//for    
		qStr.append(strArray[strArray.length-1]);
		qStrList.add(qStr.toString());		
		return qStrList;		
	}//	
	

}
