package com.example.proto.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.proto.R;
import com.example.proto.dao.ContentsDownDAO;
import com.example.proto.dao.ContentsPayAmountDAO;
import com.example.proto.dao.ContentsPayResultDAO;
import com.example.proto.dao.SimpleDAO;
import com.example.proto.dao.json.MenuJSON;
import com.example.proto.dao.json.ProgramInfoJSON;
import com.example.proto.dao.json.ProgramJSON;
import com.example.proto.dao.json.ProgramJSON.Program;
import com.example.proto.mediaplayer.MyPlayer;
import com.example.proto.socket.FtpTask.OnFtpErrorListener;
import com.example.proto.socket.FtpTask.OnFtpResponceListener;
import com.example.proto.socket.Packet;
import com.example.proto.socket.SocketRequest;
import com.example.proto.socket.SocketTask.OnSocketErrorListener;
import com.example.proto.socket.SocketTask.OnSocketResponceListener;
import com.example.proto.util.MyLog;
import com.example.proto.util.Prefs;
import com.example.proto.util.Tools;
import com.example.proto.util.ViewHolder;
import com.example.proto.view.MenuButton;
import com.example.proto.view.MyOrderButton;
import com.google.gson.Gson;

/**
 *
 */
public class MainActivity extends Activity implements
		MyPlayer.OnPlayerEventListener, OnItemClickListener {

	private static final int DEFAULT_PROGRAM_COUNT_PER_PAGE = 10;
			private MyPlayer player;
	private ProgressDialog dlg;

	//private MediaController mediaController;	

	private RelativeLayout menuLayout;// 메뉴 레이아웃
	private LinearLayout firstDepthMenuLayout;// 첫번째 메뉴 레이아웃
	private LinearLayout secondDepthMenuLayout;// 두번째 메뉴 레이아웃

	private LinearLayout programListLayout;// 프로그램 목록 레이아웃
	private ListView programListLeftListView, programListRightListView;// 프로그램 리스트
																		// 리스트뷰
	private LinearLayout detailLayout;
	private ContentsAdapter leftAdapter, rightAdapter;
	
	private MenuJSON menuDAO;//메뉴 정보
	private ProgramJSON curProgramJSON;//프로그램 리스트 정보
	HashMap<String, ProgramJSON> programInMenu = new HashMap<String, ProgramJSON>();
	private List <ProgramJSON.Program> leftProgram = new ArrayList<ProgramJSON.Program>();
	private List <ProgramJSON.Program> rightProgram = new ArrayList<ProgramJSON.Program>();

	private ProgramInfoJSON programInfoJSON;//프로그램 상세 정보
	private ProgramInfoJSON.Detail currentProgramInfo;//마지막으로 다운로드 된 프로그램 정보
	private MenuButton firstDepthBtn[], secondDepthBtn[];//메뉴 버튼
	
	private int currentProgramPage = 1;//리스트에 보여지는 프로그램의 현재 페이지
	private int programTotalPage = 0;//리스트에 보여지는 프로그램의 전체 페이지수

	// 재생중인 영상이 광고영상인지 컨텐츠영상인지에 따라 영상이 종료되었을때 처리 방식이 다르기 때문에..
	private boolean isContents = false;

	//현재 선택된 메뉴의 index 번호
	private long currentSelectedFirstMenuIndex, currentSelectedSecondMenuIndex;
	private long curContentsId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		menuLayout = (RelativeLayout) findViewById(R.id.menu_layout);//메뉴 전체 레이아웃		
		firstDepthMenuLayout = (LinearLayout) findViewById(R.id.first_depth_menu);//첫번째 메뉴 레이아웃		
		secondDepthMenuLayout = (LinearLayout) findViewById(R.id.second_depth_menu);//두번째 메뉴 레이아웃		
		programListLayout = (LinearLayout) findViewById(R.id.program_list);//프로그램 리스트(좌,우)레이아웃		
		detailLayout = (LinearLayout) findViewById(R.id.menu_detail_layout);//프로그램 상세 레이아웃

		//프로그램 좌측 리스트
		programListLeftListView = (ListView) findViewById(R.id.contents_list_1);
		leftAdapter = new ContentsAdapter(this, 0, null);
		programListLeftListView.setAdapter(leftAdapter);
		programListLeftListView.setOnItemClickListener(this);

		//프로그램 우측 리스트
		programListRightListView = (ListView) findViewById(R.id.contents_list_2);
		rightAdapter = new ContentsAdapter(this, 0, null);
		programListRightListView.setAdapter(rightAdapter);
		programListRightListView.setOnItemClickListener(this);		
		
		dlg = new ProgressDialog(this);
		// dlg.setCanceledOnTouchOutside(false);
		dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dlg.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);
		dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dlg.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

		player = (MyPlayer) findViewById(R.id.player);
		player.setDrawingCacheEnabled(true);
		
		// TODO 관고가 여러 개 있을 경우 순환 재생 로직 추가
		player.setVideoURI(Uri.parse("android.resource://" + getApplicationContext().getPackageName().toString() + "/" + R.raw.ad));
		MyLog.d("player start");
		player.start();

		player.setOnPlayerEventListener(this);		

/*		ftpManager = new FTPTransferManager();
		ftpManager.setTransferListener(new TransferListener() {

			@Override
			public void onBeginTransfer(String fileName, int id) {
			}

			@Override
			public void onEndTransfer(String fileName, int id) {
				MyLog.d("", "onEndTransfer() " + fileName);
				dlg.dismiss();

				// 메뉴가 보인다면 전체 메뉴 숨김
				if (menuLayout.getVisibility() == View.VISIBLE) {
					menuLayout.setVisibility(View.GONE);
				}

				// 이전 컨텐츠가 재생 중이었다면 이전 재생중인 화면 재생 멈춤
				if (player.isPlaying()) {
					player.stop();
				} else {
					player.start();
				}
				player.setLooping(false);
				player.setVideoURI(Uri.parse("file:"
						+ getApplicationContext().getFilesDir().toString()
						+ "/" + fileName));
			}

			@Override
			public void onUpdateTransfer(int id, int progress) {
				dlg.setProgress(progress);
			}

		});
*/		
		initView();
	}    

    /**
     * <PRE>
     * Comment : <br>     
     * 최초 메뉴 뿌려주기
     * </PRE>
     */
    public void initView() {    	
		// 첫번째 메뉴 버튼에 대한 button 동적 추가		
		boolean isExpiredObject; //프로그램 리스트가 갱신한지 1시간이 지났는지 체크하는 변수
		
		if(menuDAO == null){//한번도 프로그램 리스트를 받아오지 않은 상태임
			isExpiredObject = true;
		}else{//한번이라도 프로그램 리스트를 받아온 상태라면 해당 리스트의 유효성 확인(받아온지 1시간 이상 경과 하였다면 갱신 한다)
			if(menuDAO.isExpiredObject(System.currentTimeMillis())){
				isExpiredObject = true;
			}else{
				isExpiredObject = false;
			}			
		}
		
		if(isExpiredObject){
			//메뉴 정보 요청
			//socket으로 데이터 요청(인증 요청)
			Packet packet = new Packet(Packet.H3, Packet.OPTION_H3_MENU);//패킷 종류 선택         	
			MyLog.d("cert key = %s", Prefs.getString("CERTKEY", null));
	        packet.createPacketData(Prefs.getString("CERTKEY", null));//패킷 데이터 입력       		        
	        
	        SocketRequest.objectRequest(SocketRequest.SERV_IP, //서버 ip
	        								  SocketRequest.PORT,     //서버 port
	        								  packet,                 //전송할 데이터
	        								  SimpleDAO.class, //리턴 받을 객체 타입 선언
	        								  new OnSocketResponceListener<SimpleDAO>(){//전송이 성공하였을 경우 listener

													@Override
													public void onResponce(SimpleDAO object) {
														if(object != null){
															Gson gson = new Gson();
															String json = object.getData();
															if(json != null){
																menuDAO = gson.fromJson(json, MenuJSON.class);
																invalidateFirstMenu();
															}
														}													
													}
						        		        	
						        		      },  new OnSocketErrorListener<SimpleDAO>(){//전송이 실패하였을 경우 listener
						
													@Override
													public void onError(int failCode, SimpleDAO object) {
														// TODO 인증 실패 메시지 표시
														MyLog.d("socket error : %d", failCode);
													}
						        		        	
						        		      });
		}
		
		
	}
    
    /**
     * <PRE>
     * Comment : <br>
     * 첫번째 메뉴 그리기
     * </PRE>
     */
    public void invalidateFirstMenu(){
    	// 현재는 첫번째 depth 메뉴 버튼을 임의로 구성하지만 추후에는 동적으로 구성해야 함
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		
		firstDepthBtn = new MenuButton[menuDAO.getMenus().size()];

		for (int i = 0; i < firstDepthBtn.length; i++) {			
			int currentBtnId = Tools.generateViewId();			
			firstDepthBtn[i] = new MenuButton(this);		
			firstDepthBtn[i].setFocusable(true);			
			firstDepthBtn[i].setId(currentBtnId);			
			firstDepthBtn[i].setDistintId(i);//구분할 수 있도록 인덱스 저장
			firstDepthBtn[i].setText(menuDAO.getMenus().get(i).getFirstMenuName());
			firstDepthMenuLayout.addView(firstDepthBtn[i]);
			firstDepthBtn[i].setLayoutParams(param);
			if(i == 1){
				firstDepthBtn[i].requestFocus();
			}
			firstDepthBtn[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// 두번째 메뉴에 대한 button 동적 추가
					
					//현재 선택된 메뉴의 인덱스 번호 저장
					currentSelectedFirstMenuIndex = ((MenuButton)view).getDistintId();					
					
					//메뉴 영역의 변경이 있을때만 다시 그리도록 하는 코드를 만들어야 함					
					invalidateSecondMenu((int)currentSelectedFirstMenuIndex);
					
					// 프로그램 리스트가 열려있다면 숨김
					if (programListLayout.getVisibility() == View.VISIBLE) {
						programListLayout.setVisibility(View.GONE);
					}					
				}

			});
		}
    }

    /**
     * <PRE>
     * Comment : <br>
     * 두번째 메뉴 그리기
     * </PRE>
     */
	public void invalidateSecondMenu(final int firstMenuIndex) {
		// 기존에 동적 추가된 버튼이 한개라도 있을 경우 해당 view 삭제
		if (secondDepthMenuLayout.getChildCount() > 0) {
			this.secondDepthMenuLayout.removeAllViews();
		}

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);

		secondDepthBtn = new MenuButton[menuDAO.getMenus().get(firstMenuIndex).getSecondMenu().size()];
		for (int i = 0; i < menuDAO.getMenus().get(firstMenuIndex).getSecondMenu().size(); i++) {
			secondDepthBtn[i] = new MenuButton(this);
			String name = menuDAO.getMenus().get(firstMenuIndex).getSecondMenu().get(i).getName();
			String code = menuDAO.getMenus().get(firstMenuIndex).getSecondMenu().get(i).getCode();
			secondDepthBtn[i].setDistintId(i);//구분할 수 있도록 인덱스 저장
			secondDepthBtn[i].setText(name);
			secondDepthBtn[i].setName(name);
			secondDepthBtn[i].setCode(code);
			//secondDepthBtn[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.btn));
			
			secondDepthMenuLayout.addView(secondDepthBtn[i]);
			if(i == 0){//최초에 포커스를 첫번째 버튼으로 줘야 함
				secondDepthBtn[i].requestFocus();
			}
			secondDepthBtn[i].setLayoutParams(param);
			secondDepthBtn[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					
					//선택된 두번째 단계의 메뉴 인덱스 번호 저장
					currentSelectedSecondMenuIndex = ((MenuButton) view).getDistintId();
					
					//프로그램 리스트를 받아온다.
					invalidateProgramList(menuDAO.getMenus().get((int)currentSelectedFirstMenuIndex).getSecondMenu().get((int)currentSelectedSecondMenuIndex).getCode());
				}
			});
		}
		secondDepthMenuLayout.setVisibility(View.VISIBLE);

	}
	
	
	/**
	 * <PRE>
	 * Comment : <br>
	 * 프로그램 리스트 뿌려주기
	 * </PRE>
	 * @param menuCode 선택된 두번째 메뉴 코드
	 */
	private void invalidateProgramList(final String secondMenuCode){
		
		boolean isExpiredObject; //프로그램 리스트가 갱신한지 1시간이 지났는지 체크하는 변수

		curProgramJSON = programInMenu.get(secondMenuCode);
		if(curProgramJSON == null){//한번도 프로그램 리스트를 받아오지 않은 상태임
			isExpiredObject = true;
		}else{//한번이라도 프로그램 리스트를 받아온 상태라면 해당 리스트의 유효성 확인(받아온지 1시간 이상 경과 하였다면 갱신 한다)
			if(curProgramJSON.isExpiredObject(System.currentTimeMillis())){
				isExpiredObject = true;
			}else{
				isExpiredObject = false;
			}			
		}
		
		if(isExpiredObject){
			//socket으로 데이터 요청(program_list)
			Packet packet = new Packet(Packet.H3, Packet.OPTION_H3_PROGRAM);//패킷 종류 선택 			
	        packet.createPacketData(Prefs.getString("CERTKEY", null), secondMenuCode);//패킷 데이터 입력 (인증키, 메뉴 코드)      

	        
			SocketRequest.objectRequest(SocketRequest.SERV_IP, //서버 ip
					  SocketRequest.PORT,     //서버 port
					  packet,                 //전송할 데이터
					  SimpleDAO.class, //리턴 받을 객체 타입 선언
					  new OnSocketResponceListener<SimpleDAO>(){//전송이 성공하였을 경우 listener

							@Override
							public void onResponce(SimpleDAO object) {
								Gson gson = new Gson();
								String json = object.getData();
								// TODO 만약 데이타가 없다면?
								curProgramJSON = gson.fromJson(json, ProgramJSON.class);								
								curProgramJSON.setCreateTime(System.currentTimeMillis());//새로 갱신된 시간 입력	
								programInMenu.put(secondMenuCode, curProgramJSON);
								//화면에 뿌려줄 전체 프로그램 페이지 수 계산
								int totalItemSize = curProgramJSON.getPrograms().size();
								programTotalPage = totalItemSize / DEFAULT_PROGRAM_COUNT_PER_PAGE;
								if(curProgramJSON.getPrograms().size() % DEFAULT_PROGRAM_COUNT_PER_PAGE > 0)
									programTotalPage++;								
								
								//해당 내용을 리스트에 뿌려 줘야 함
								setProgramListData(currentProgramPage, totalItemSize);
								
							}
	  		        	
	  		      },  new OnSocketErrorListener<SimpleDAO>(){//전송이 실패하였을 경우 listener

							@Override
							public void onError(int failCode, SimpleDAO object) {
								// TODO program_list 수신 실패 메시지 표시
								MyLog.d("socket error : %d", failCode);
							}
	  		        	
	  		      });			
		}else{
			
			setProgramListData(currentProgramPage,  curProgramJSON.getPrograms().size());
		}	
		
		// 프로그램 리스트가 보이도록 한다.
		programListLayout.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * <PRE>
	 * Comment : <br>
	 * 서버로부터 받아온 프로그램 리스트 좌,우 화면에 맞게 아이템 세팅
	 * (서버로부터 각 페이지 데이터를 각각 요청하는것이 아니라 한번에 받아오므로 분기 필요)
	 * </PRE>
	 * @param page 뿌려줄 페이지 번호
	 * @param totalItemCount 전체 아이템 크기
	 */
	private void setProgramListData(final int page, final int totalItemCount){
		
		//기존 리스트 삭제
		leftProgram.clear();
		rightProgram.clear();
		
		int i = DEFAULT_PROGRAM_COUNT_PER_PAGE * (page - 1);
		if(page == 1){//첫번째 페이지
			if(totalItemCount < DEFAULT_PROGRAM_COUNT_PER_PAGE){
				for(; i < (totalItemCount % DEFAULT_PROGRAM_COUNT_PER_PAGE); i++){
					distinctionProgramList(i);
				}
			}else{
				int j = i + DEFAULT_PROGRAM_COUNT_PER_PAGE;
				for(; i < j; i++){				
					distinctionProgramList(i);
				}
			}
			
		}else{//2~n번째 페이지
			if(programTotalPage / (DEFAULT_PROGRAM_COUNT_PER_PAGE * (page - 1)) > 0){		
				int j = i + DEFAULT_PROGRAM_COUNT_PER_PAGE;
				for(; i < j; i++){				
					distinctionProgramList(i);
				}
			}else{
				for(; i < (totalItemCount % DEFAULT_PROGRAM_COUNT_PER_PAGE); i++){
					distinctionProgramList(i);
				}
			}
		}
		//리스트 갱신
		leftAdapter.updateItems(leftProgram);
		rightAdapter.updateItems(rightProgram);
	}
	
	/**
	 * <PRE>
	 * Comment : <br>
	 * 프로그램 리스트의 왼쪽에 보여줄지 오른쪽에 보여줄지 확인해서 넣어줌
	 * </PRE>
	 * @param index
	 */
	private void distinctionProgramList(final int index){
		ProgramJSON.Program program = curProgramJSON.getPrograms().get(index);
		int temp = index % DEFAULT_PROGRAM_COUNT_PER_PAGE;
		if (temp >= (DEFAULT_PROGRAM_COUNT_PER_PAGE / 2)) {			
			rightProgram.add(program);
		} else {			
			leftProgram.add(program);
		}
	}


	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause(){
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		if (menuLayout.getVisibility() == View.VISIBLE) {// 메뉴 레이아웃이 보일경우
			if (firstDepthMenuLayout.getVisibility() == View.VISIBLE) {//첫번째 메뉴가 보일때
				if (secondDepthMenuLayout.getVisibility() == View.VISIBLE) {//두번째 메뉴가 보일때
					if (programListLayout.getVisibility() == View.VISIBLE) {//프로그램 리스트가 보일때						
						programListLayout.setVisibility(View.GONE);
						secondDepthBtn[(int)currentSelectedSecondMenuIndex].requestFocus(); //마지막으로 선택된 두번째 메뉴 버튼 포커스
					} else {						
						secondDepthMenuLayout.setVisibility(View.GONE);
						firstDepthBtn[(int)currentSelectedFirstMenuIndex].requestFocus(); //마지막으로 선택된 첫번째 메뉴 버튼 포커스
					}
				}
				else
					finish();
			}
		} else {//상세 메뉴 레이아웃이 보일 경우
			menuLayout.setVisibility(View.VISIBLE);
			detailLayout.setVisibility(View.GONE);
			
			secondDepthBtn[(int)currentSelectedSecondMenuIndex].requestFocus(); //마지막으로 선택된 두번째 메뉴 버튼 포커스
		}
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public void onMediaPlayerError(MediaPlayer mediaPlayer, int frError,
			int implError) {
		int messageId;

		if (frError == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
			messageId = android.R.string.VideoView_error_text_invalid_progressive_playback;
		} else {
			messageId = android.R.string.VideoView_error_text_unknown;
		}

		new AlertDialog.Builder(getApplicationContext())
				.setTitle(android.R.string.VideoView_error_title)
				.setMessage(messageId)
				.setPositiveButton(android.R.string.VideoView_error_button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).setCancelable(false).show();
	}


	@Override
	public void onMediaPlayerComplete(MediaPlayer mp) {

		if (isContents == true) {
			MyLog.d("now i'm gonna ask you if you continue watching");
			//다음 회차가 있다면 팝업창 띄움
			if(programInfoJSON.getDetails().indexOf(currentProgramInfo) < programInfoJSON.getDetails().size()){
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("알림")
				.setMessage("다음 회를 계속 시청 하시겠습니까?")
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						//해당 되는 다음회차에 대한 컨텐츠 요청
						setProgramDetailLayout(true);
					}
				})
				.setNegativeButton("아니요",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// TODO 기본광고 외에 다른 광고를 이미 백그라운드로 받은 것이 있는지 확인하고
								// 있으면 광고를 돌려가면서 실행하는 모듈을 호출...

							}
						}).show(); 
			}			
		}
		else { // 광고일 경우
			
		}

		
	}

	@Override
	public void onMediaPlayerStarted() {
		MyLog.d("media player onStarted()");
	}
	
	@Override
	public void onMediaPlayerError(int errorType) {
		
	}
	
	@Override
	public void onMediaPlayerPrepared(MediaPlayer mp) {
	}

	//프로그램 좌,우 리스트 아답터
	private class ContentsAdapter extends ArrayAdapter<ProgramJSON.Program> {

		private List<ProgramJSON.Program> items;
		private Context context;

		public ContentsAdapter(Context context, int textViewResourceId,
				ArrayList<ProgramJSON.Program> items) {
			super(context, textViewResourceId);
			this.items = items;
			this.context = context;
		}
		
		public void updateItems(List<ProgramJSON.Program> items){
			this.items = items;
			notifyDataSetChanged();
		}
		
		@Override 
		public Program getItem(int position){
			if(items != null){
				return items.get(position);
			}
			return null;
		}
		 

		@Override
		public int getCount() {
			if (items != null) {
				return items.size();
			} else {
				return 0;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.content_row,	parent, false);
			}
			
			final TextView textView = ViewHolder.get(view, R.id.contents_name);
			textView.setText(items.get(position).getName());
			return view;
		}	
	}

	// 리스트뷰 아이템 클릭 리스너
	@Override
	public void onItemClick(AdapterView<?> adapterView, View clickedView,
			int position, long id) {

		Packet packet = new Packet(Packet.H3, Packet.OPTION_H3_PROGRAM_DETAIL);//패킷 종류 선택 
		String code = ((ProgramJSON.Program)adapterView.getAdapter().getItem(position)).getCode();	
		MyLog.d("program request code : %s", code);
        packet.createPacketData(Prefs.getString("CERTKEY", null), code);//패킷 데이터 입력 (인증키, 프로그램 코드)     
        
		//해당 아이템의 프로그램 정보를 요청 한다.
		SocketRequest.objectRequest(SocketRequest.SERV_IP, //서버 ip
				  SocketRequest.PORT,     //서버 port
				  packet,                 //전송할 데이터
				  SimpleDAO.class, //리턴 받을 객체 타입 선언
				  new OnSocketResponceListener<SimpleDAO>(){//전송이 성공하였을 경우 listener

						@Override
						public void onResponce(SimpleDAO object) {
							if(object != null){
								Gson gson = new Gson();
								String json = object.getData();
								programInfoJSON = gson.fromJson(json, ProgramInfoJSON.class);								
								programInfoJSON.setCreateTime(System.currentTimeMillis());//새로 갱신된 시간 입력							
								
								//가져온 program 상세 정보 뷰에 뿌려주기
								int programInfoSize = programInfoJSON.getDetails().size();		
								if(programInfoSize > 1){//프로그램이 회차 정보를 가지고 있을 경우
									setProgramDetailLayout(true);
								}else{//프로그램이 회차 정보를 가지고 있지 않을 경우
									setProgramDetailLayout(false);
								}
							}
							
						}
		        	
		      },  new OnSocketErrorListener<SimpleDAO>(){//전송이 실패하였을 경우 listener

						@Override
						public void onError(int failCode, SimpleDAO object) {
							// TODO 메시지 표시
							MyLog.d("socket error : %d", failCode);
						}
		        	
		      });		
	}

	
	/**
	 * <PRE>
	 * Comment : <br>
	 * 프로그램 상세 정보 데이터 세팅
	 * </PRE>
	 * @param isProgramListHaveOrder 회차 정보가 있는지 여부
	 */
	private void setProgramDetailLayout(final boolean isProgramListHaveOrder){
		
		View view = null;
		
		//기존에 있던 레이아웃을 모두 제거 함
		if (detailLayout.getChildCount() > 0) {
			detailLayout.removeAllViews();
		}	
		
		if(isProgramListHaveOrder == false){//회차 정보가 없을 경우
			view = LayoutInflater.from(this).inflate(
					R.layout.detail_content_view1, detailLayout, true);
			
			//받아온 데이터를 가지고 화면 세팅..
			
			//OK를 누르면 해당 프로그램 선택
			
		}else{//회차 정보가 있을 경우
			view = LayoutInflater.from(this).inflate(
					R.layout.detail_content_view2, detailLayout, true);
			
			final HorizontalScrollView pagerLayout = (HorizontalScrollView) view
					.findViewById(R.id.scroll_layout);
			
			final LinearLayout btnLayout = new LinearLayout(getApplicationContext());
			btnLayout.setOrientation(LinearLayout.HORIZONTAL);
			
			final LinearLayout layout = (LinearLayout) view
					.findViewById(R.id.layout2);
			
			final Button left = (Button)layout.findViewById(R.id.left_scroll);
			left.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View paramView) {
					int delta = pagerLayout.getScrollX() - 64;//64는 스크롤바안에 들어가는 버튼의 크기입니다.	
					//currentScreen = Math.abs(delta / 64);
					pagerLayout.smoothScrollTo(delta, pagerLayout.getScrollY());					
				}
				
			});
			final Button right = (Button)layout.findViewById(R.id.right_scroll);
			right.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View paramView) {
					int delta = pagerLayout.getScrollX() + 64;				
					//currentScreen = Math.abs(delta / 64);
					pagerLayout.smoothScrollTo(delta, pagerLayout.getScrollY());			
					
				}
				
			});
			
			LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(64, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			// 동적으로 회차의 순서대로 버튼을 넣어줌 
			int programDetailCount = programInfoJSON.getDetails().size();
			MyOrderButton btn[] = new MyOrderButton[programDetailCount];
			for (int i = 0; i < btn.length; i++) {		
				ProgramInfoJSON.Detail detail = programInfoJSON.getDetails().get(i);
				if (i + 1 != detail.getNo()) {
					// TODO 다양한 경우의 수를 테스트해볼 것..
					continue;
				}
				btn[i] = new MyOrderButton(this);
				btn[i].setDistintId(detail.getId());
				btn[i].setNo(detail.getNo());
				btn[i].setText(Integer.toString(i));
				btn[i].setBackgroundResource(R.drawable.btn);
				btn[i].setLayoutParams(btnParams);				
				btnLayout.addView(btn[i]);
				btn[i].requestFocus();//최초 생성된 버튼에 포커스
				
				btn[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View view) {
						//포커스가 있을때 OK버튼을 눌러 해당 프로그램 선택						
						
						//해당 프로그램 결제 요청
						Packet packet = new Packet(Packet.PAY, Packet.OPTION_NONE);//패킷 종류 선택 
						curContentsId = ((MyOrderButton) view).getDistintId();
						
						packet.createPacketData(Prefs.getString("CERTKEY", null), curContentsId);//패킷 데이터 입력 (인증키, 프로그램 코드)    
						currentProgramInfo = programInfoJSON.getDetails().get(((MyOrderButton)view).getNo());
						SocketRequest.objectRequest(SocketRequest.SERV_IP, //서버 ip
								  SocketRequest.PORT,     //서버 port
								  packet,                 //전송할 데이터
								  ContentsPayAmountDAO.class, //리턴 받을 객체 타입 선언
								  new OnSocketResponceListener<ContentsPayAmountDAO>(){//전송이 성공하였을 경우 listener

										@Override
										public void onResponce(ContentsPayAmountDAO object) {
											// TODO Auto-generated method stub
											if(object != null){												
												double payment = object.getAmount();
												
												if (payment != 0.0) {
													// 결제 여부 문의
													AlertDialog.Builder alt_pay = new AlertDialog.Builder(getApplicationContext());
													alt_pay.setMessage("결제할 금액이 " + payment + "$입니다. 결제하시겠습니까?").setCancelable(
															false).setPositiveButton("예",  new DialogInterface.OnClickListener() {
														
														@Override
														public void onClick(DialogInterface dialog, int which) {
															notifyPayment(true);
															
														}
													}).setNegativeButton("아니오",  new DialogInterface.OnClickListener() {
														
														@Override
														public void onClick(DialogInterface dialog, int which) {
															notifyPayment(false);
															dialog.cancel();
														}
													});
													AlertDialog alert = alt_pay.create();
													alert.setTitle("유료 컨텐츠");
													alert.show();
												}
												//detailLayout.setVisibility(View.GONE);
												//menuLayout.setVisibility(View.GONE);
												// TODO check pay and process it.
												notifyPayment(true);
												//requestContentsdownInfo(contentsId);
											}
											else {
												// TODO 에러 처리.
												MyLog.d("object is null!");
											}
											
										}
						        	
						      },  new OnSocketErrorListener<ContentsPayAmountDAO>(){//전송이 실패하였을 경우 listener

										@Override
										public void onError(int failCode, ContentsPayAmountDAO object) {
											// TODO Auto-generated method stub
											MyLog.d("socket error : %d", failCode);
										}
						        	
						      });	
					}				

				});
			}
			pagerLayout.addView(btnLayout);
		}	
				
		detailLayout.setVisibility(View.VISIBLE);
		menuLayout.setVisibility(View.GONE);
	}

	private void notifyPayment(boolean payment)
	{
		// TODO 여기서 requestContentDownInfo를 호출해야 함.
		//해당 프로그램 결제 요청
		Packet packet = new Packet(Packet.PAY, Packet.OPTION_NONE);//패킷 종류 선택 
				
		if (payment == true) {
			packet.createPacketData(Prefs.getString("CERTKEY", null), curContentsId);//패킷 데이터 입력 (인증키, 컨텐츠코드)    			
		}
		else {
			packet.createPacketData(Prefs.getString("CERTKEY", null), 0);//패킷 데이터 입력 (인증키, 0)    						
		}
		SocketRequest.objectRequest(SocketRequest.SERV_IP, //서버 ip
				  SocketRequest.PORT,     //서버 port
				  packet,                 //전송할 데이터
				  ContentsPayResultDAO.class, //리턴 받을 객체 타입 선언
				  new OnSocketResponceListener<ContentsPayResultDAO>(){//전송이 성공하였을 경우 listener

						@Override
						public void onResponce(ContentsPayResultDAO object) {
							// TODO Auto-generated method stub
							if(object != null){
								if (object.getFileID() != 0) {
									detailLayout.setVisibility(View.GONE);
									menuLayout.setVisibility(View.GONE);
									requestContentsdownInfo(object.getFileID());									
								}
							}
							else {
								// TODO 에러 처리.
								MyLog.d("object is null!");
							}
							
						}
		        	
		      },  new OnSocketErrorListener<ContentsPayResultDAO>(){//전송이 실패하였을 경우 listener

						@Override
						public void onError(int failCode, ContentsPayResultDAO object) {
							// TODO Auto-generated method stub
							MyLog.d("socket error : %d", failCode);
						}
		        	
		      });	

	}

	
	/**
	 * <PRE>
	 * Comment : <br>
	 * 컨텐츠 다운로드 정보 요청
	 * </PRE>
	 * @param no 다운로드 할 컨텐츠의 회차
	 * @param contentsFileId 다운로드할 컨텐츠 파일 아이디
	 */
	private void requestContentsdownInfo(final long contentsFileId){
		//컨텐츠 다운로드 정보 요청
		Packet packet = new Packet(Packet.FTP, Packet.OPTION_NONE);//패킷 종류 선택 
		MyLog.d("------------------------ now ask for ftp info contensfileid %d", contentsFileId);

		packet.createPacketData(Prefs.getString("CERTKEY", null), contentsFileId);//패킷 데이터 입력 (인증키, 컨텐츠 파일 아이디)    
		
		SocketRequest.ftpRequest(SocketRequest.SERV_IP, //서버 ip
				  SocketRequest.PORT,     //서버 port
				  getApplicationContext(),
				  dlg,
				  contentsFileId,
				  player,
				  packet,                 //전송할 데이터
				  ContentsDownDAO.class, //리턴 받을 객체 타입 선언
				  new OnFtpResponceListener(){//전송이 성공하였을 경우 listener
						@Override
						public void onResponce() {
							MyLog.d("ftp download done");
							String path = new String("file:" + getApplicationContext().getFilesDir().toString()	+ "/" + String.valueOf(contentsFileId));
							MyLog.d("download file path is %s", path);
/*							AlertDialog.Builder alert = new AlertDialog.Builder(com.example.proto.activity.MainActivity.this);
							alert.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							alert.setMessage("path:" + path);
							alert.show();
*/							// 메뉴가 보인다면 전체 메뉴 숨김

							// 이전 컨텐츠가 재생 중이었다면 이전 재생중인 화면 재생 멈춤
							if (player.isPlaying()) {
								player.stop();
							}
							player.setLooping(false);
							player.setVideoURI(Uri.parse(path));
							player.start();
							dlg.dismiss();
						}
		        	
		      },  new OnFtpErrorListener(){//전송이 실패하였을 경우 listener

						@Override
						public void onError(int failCode) {
							// TODO ftp 수신 실패 메시지 표시
							MyLog.d("socket error : %d", failCode);
						}
		        	
		      });
	}

	
	
}
