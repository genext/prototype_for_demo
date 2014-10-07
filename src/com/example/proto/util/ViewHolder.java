/** 
 * <PRE>
 * Comment : <br>
 * 동적 ViewHolder class(모든 view들은 View를 상속 받고 있기 때문에 SparseArray를 통해 ChildView의 ID가 없다면 findViewById를 통해 put해주고 있으면 get으로 가져오는 방식)
 * @author : drugghanzi@funnc.com
 * @version 1.0.0
 * @see 
 * </PRE>
*/
package com.example.proto.util;

/* drugghanzi 
 * 안드로이드의 기본 단위는 view입니다. 부모 view에 childview가 속해있는 형태로 구성되어 있으며 해당 childview는 id(xml에 정의된)를 통해 접근 가능합니다.
 * 소스를 보시면 아시겠지만 viewHolder라는 더미를 통해 해당 부모뷰를 가져오고 부모뷰에 해당 자식뷰가 있으면 id를 통해 가져오고 없으면 put해서 다음번에 가져오는 방식입니다.			
*/			
import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
	
	// drugghanzi 해당 함수는 static 입니다. 
	// jkoh 생성자가 없는데. 자바도 c++처럼 컴파일러가 알아서 기본 생성자를 만드나? 그런데 아래 코드를 보면 viewHolder를 초기화하는데...
	@SuppressWarnings("unchecked") // jkoh 이 함수 파악하기..
	public static <T extends View> T get(View view, int id){
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if(viewHolder == null){
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		
		// jkoh setTag, getTag 의미 파악하기..
		View childView = viewHolder.get(id);
		if(childView == null){
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		
		return (T) childView;
	}
}
