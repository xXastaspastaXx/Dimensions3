package me.xxastaspastaxx.dimensions.builder;

public class CreatePortalOptions {

//	public String structureName = "default";
//
//	public boolean strucureBidirectional = false;
//
//	public boolean structureDestroyOnSpawn = false;
//
//	public boolean structureKeepBlockData = false;

//	public HashMap<Integer, CreateStructureAction> actions = new HashMap<Integer, CreateStructureAction>();
	
//	public ArrayList<StructureActivator> activators = new ArrayList<StructureActivator>(Arrays.asList(new StructureActivator[] {StructureActivator.PLAYER_BLOCK_PLACE}));

	
//	
//	//Actions methods
//	public void moveAction(int selectedMove, int itemIndex) {
//		
//		if (itemIndex<selectedMove) {
//			for (int i=selectedMove;i>itemIndex;i--) {
//				swapAction(i-1, i);
//			}
//		} else {
//			for (int i=selectedMove;i<itemIndex;i++) {
//				swapAction(i+1, i);
//			}
//		}
//		
//	}
//	
//	public void swapAction(int selectedMove, int itemIndex) {
//		CreateStructureAction temp = actions.get(itemIndex);
//		actions.put(itemIndex, actions.get(selectedMove));
//		actions.put(selectedMove, temp);
//	}
//
//	public void remove(CreateStructureAction selectedAction) {
//		boolean moveBack = false;
//		for (int i=0;i<actions.size();i++) {
//			if (moveBack) {
//				actions.put(i, actions.get(i+1));
//			} else if (actions.get(i)==selectedAction) {
//				moveBack = true;
//				actions.put(i, actions.get(i+1));
//			}
//		}
//		
//		actions.remove(actions.size()-1);
//	}
//
//	public Map<Integer, Map<String, Object>> serializeActions() {
//		Map<Integer, Map<String, Object>> res = new LinkedHashMap<Integer, Map<String, Object>>();
//		
//		for (int i=0;i<actions.size();i++) {
//			res.put(i, actions.get(i).serialize());
//		}
//		
//		return res;
//	}
//	//Actions methods


}
