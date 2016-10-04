#include <bits/stdc++.h>
#include "Board.h"
#define s(n) scanf("%d",&n)

using namespace std;

/* Global Vars - all underscore naming */
int moves_me = 0,moves_opp = 0;
int cut_off = 4;
float time_limit = 0;
bool finish_game = false;
Board board;

string maxMove(int p,Board board, float& alpha, float& beta, int level);
string minMove(int p,Board board, float& alpha, float& beta, int level);

void initialise(){
	int player_me,board_size;
	cin >> player_me;
	cin >> board_size;
	cin >> time_limit;
	board = Board(board_size,player_me);
	cerr << "Player: "<<board.player_color[0]<<" | board size: "<<board.size<<" | time_limit: "<<time_limit<<endl;
}

string parseMove(string s, float& num){
	int k=0;
	bool neg = false;
	if(s[0] == '-'){
		neg = true;
		k++;
	}
	string number="";
	while(s[k]!='#'){
		number += s[k];
		k++;
	}
	num = stof(number);
	k++;
	if(neg){
		num = -num;
	}
	string action = "";
	for ( ; k < s.length(); ++k){
		action += s[k];
	}
	return action;
}

void placePiece(int p, string cmd, Board& board){
	char first_char = cmd[0];
	int pos = (cmd[1] - 'a') * board.size + (cmd[2] - '0' - 1);			// pos => i*n + j-1
    if(first_char == 'C'){
   		board.p_caps[p]--;
   	}else{
   		board.p_flats[p]--;
   	}
   	BoardUnit bu = BoardUnit(board.player_color[p],(first_char=='F')?1:((first_char=='S')?2:3));
	board.addPiece(pos, bu);
}


void moveStack(string cmd, Board& board){
	int pieces_to_move = 0;
	int i=0;
	while(cmd[i]>='0' && cmd[i] <='9'){
		pieces_to_move = pieces_to_move*10 + cmd[i]-'0';
		i++;
	}
	// cout << "Number of pieces to move: "<<pieces_to_move<<endl;
    int posn_alphabet = (int)(cmd[i++] - 'a');
    int posn_numeric = (int)(cmd[i++] - '0' - 1);
    int pos = posn_alphabet * board.size + posn_numeric ;						//position of that piece on board
    char direction = (cmd[i++]);
   
   	if(board.b[pos].size() < pieces_to_move || pieces_to_move > board.size){
    	cout << "Invalid move"<<endl;
    	return;
    }
    int next = 0;
    int add_next = 0;
    if(direction == '-'){
    	next = -1;
    	add_next = -1;
    }else if(direction == '+'){
    	next = 1;
    	add_next = 1;
    }else if(direction == '>'){
    	next = board.size;
    	add_next = board.size;
    }else if(direction == '<'){
    	add_next = -board.size;
    	next = -board.size;
    }

    // cerr << "about to move:: "<<endl;
	int idx = board.b[pos].size() - pieces_to_move;
	// cout<<"total: "<< board.b[position].size()<<"  ||  idx: "<<idx<<endl;
	for(int j=0;j<pieces_to_move;j++){
		int num = (int) (cmd[i] - '0');
		while(num--){
			if(j == pieces_to_move - 1 && board.b[pos + next].size()>0 && board.b[pos + next].back().kind ==2 ){
				board.b[pos + next].back().kind == 1;		// standing stone will be flattened by thecapstone. Validity of move was already checked before u just need tp flatten 
			}
			board.b[pos + next].push_back(board.b[pos][idx++]);
			j++;
		}
		next += add_next;
	}

	idx = board.b[pos].size() - pieces_to_move;
	board.b[pos].resize(idx);
}

bool isCorner(int r,int c){
	if(r == 0 && c==0){return true;}
	else if(r == 0 && c== board.size-1){return true;}
	else if(r == board.size-1 && c== 0){return true;}
	else if(r == board.size-1 && c == board.size-1){return true;}
	return false;
}

bool isEdge(int r,int c){
	if(r==0 || c==0 || r==board.size-1 || c==board.size-1){return true;}
	return false;
}

float weightedTopStoneCount(Board& board){//returns number of squares owned by me and that by opponent in an array [p_me,p_opp]
    // int [] result = new int[2];
    float p_me=0;
    float p_opp = 0;
    float points = 0;
    int pos = 0;
    for(int i = 0; i< board.size; i++){
        for(int j = 0; j<board.size; j++){
        	pos = (i*board.size)+j;
        	if(board.b[pos].size() > 0){ 
        		
        		//weights for corners edges and middle
        		if( isCorner(i,j)) {points = 2;}
        		else if (isEdge(i,j)){points = 3;}
        		else{ points = 4;}
        		
        		//weights for flat,stand and cap
        		// if(board.b[pos].back().kind == 1){ 
        		// 	if(moves_me<5 || moves_me > 12){
        		// 		points += 2.5;	
        		// 	}else{
        		// 		points += 1.5;	
        		// 	}
        		// }else if(board.b[pos].back().kind == 2){		//stading stone 
        		// 	if(moves_me > 12){
        		// 		points += 1.5;	
        		// 	}else{
        		// 		points += 2;	
        		// 	}
        		// }else{
        		// 	if(moves_me > 5){
        		// 		points += 3;	
        		// 	}else{
        		// 		points += 2;	
        		// 	}
        		// }
        		
        		if(board.b[pos].back().color == board.player_color[0]){ p_me += points;}
        		else{ p_opp += points;}
        	}
        }
    }
    return (p_me - p_opp);

}

inline float countStackElements(Board& board,int player,int pos){
	int stack_size = board.b[pos].size(), num=0;
	for (int i = 0; i < stack_size; ++i){
		if(board.b[pos][i].color == board.player_color[player]){
			num++;
		}
	}
	return num;
}

float weightedStackSum(Board& board){
        float p_me=0;
        float p_opp = 0;
        float points = 0;
        int pos = 0;
        int num_owned_me=0;
        int num_owned_opp=0;
        for(int i = 0; i< board.size; i++){
            for(int j = 0; j< board.size; j++){
            	pos = (i*board.size)+j;
            	int stack_size = board.b[pos].size();
            	if( stack_size > 0 ){
            		if(board.b[pos].back().color == board.player_color[0]){						//"\n"[0] -> me
            			num_owned_me = countStackElements(board,0,pos);
            			num_owned_opp = countStackElements(board,1,pos);
            			p_me += stack_size * (num_owned_me + num_owned_opp/2);
            		}else{
            			num_owned_me = countStackElements(board,1,pos);
            			num_owned_opp = countStackElements(board,0,pos);
            			p_opp += stack_size * (num_owned_me + num_owned_opp/2);
            		}
            	}
            }
        }
        // result[0]=p_me;
        // result[1] = p_opp;
        return (p_me - p_opp);
}

float getMaxChainLengthDiff(Board& board){//returns the length of the maximum length chain of mine and opp's
    // int[] result = new int[2];
    float max_me=0,cur_me=0,max_opp=0,cur_opp=0;
    int pos = 0;

    //going along columns
    for(int i = 0; i< board.size; i++){
        for(int j = 0; j< board.size; j++){
            pos = (i*board.size)+j;
            if(j==0){
            	cur_me = cur_opp = 0;
            }
            int stack_size = board.b[pos].size();
            if(stack_size == 0 ){
            	cur_me = 0;
            	cur_opp = 0;
            }else{
            	if(board.b[pos].back().color == board.player_color[0] && board.b[pos].back().kind != 2){
            		cur_me++;
            		cur_opp = 0;
            		max_me = max(max_me,cur_me);
            	}else if(board.b[pos].back().color == board.player_color[1] && board.b[pos].back().kind != 2){
            		cur_me = 0;
            		cur_opp++;
            		max_opp = max(max_opp,cur_opp);
            	}
            }
        }
    }

    for(int j = 0; j< board.size; j++){
        for(int i = 0; i< board.size; i++){
            pos = (i*board.size)+j;
            if(i==0){
            	cur_me = cur_opp = 0;
            }
            int stack_size = board.b[pos].size();
            if(stack_size == 0 ){
            	cur_me = 0;
            	cur_opp = 0;
            }else{
            	if(board.b[pos].back().color == board.player_color[0] && board.b[pos].back().kind != 2){
            		cur_me++;
            		cur_opp = 0;
            		max_me = max(max_me,cur_me);
            	}else if(board.b[pos].back().color == board.player_color[1] && board.b[pos].back().kind != 2){
            		cur_me = 0;
            		cur_opp++;
            		max_opp = max(max_opp,cur_opp);
            	}
            }
        }
    }
    // if(max_opp == board.size && max_me==board.size){ return INT_MAX;}
    if(max_opp == board.size){return INT_MIN;}
    else if(max_me == board.size){return INT_MAX;}
    else if(max_opp == board.size - 1){return -10;}
    else if(max_me == board.size - 1){return 10;}
    // result[0]=max_me;
    // result[1] = max_opp;
    // return result;
    return (max_me-max_opp)*(max_me-max_opp);
}


float evaluate(Board& board){
	float result = 0.0;
	result += weightedTopStoneCount(board);
	result += board.p_flats[1] - board.p_flats[0];
	result += weightedStackSum(board);
	result += getMaxChainLengthDiff(board);
	// for (int i = 1; i < num_players; ++i){
		// result -= (this.getStackHeight(i)+this.getOwnedSquares(i)+this.getMaxChainLength(i));
	// }
    return result;
}

int sumDigits(int num){
	int sum = 0;
	// int tmp=0;
	while(num!=0){
		sum += num%10;
		num /=10;
	}
	return sum;
}

int numDigits(int num){
	int n=0;
	while(num!=0){
		n++;
		num/=10;
	}
	return n;
}

vector<int> partition(int num){
	vector <int> part_list;
	vector<int> part_smaller;

	part_list.push_back(num);
	for(int i=1;i<num;i++){
		part_smaller = partition(num-i);
		for(int j=0;j < part_smaller.size();j++){
			part_list.push_back( i*pow(10,numDigits(part_smaller[j])) + part_smaller[j]);
		}
	}
	return part_list;
}



bool checkValid(Board& board,int pos,char dir,int part){
	int add_next=0;
	if(dir=='<'){
		add_next = -board.size;
	}else if(dir == '>'){
		add_next = board.size;
	}else if(dir == '+'){
		add_next = 1;
	}else{
		add_next = -1;
	}
	int next = pos;
	while(part!=0){
		next += add_next;

		if(board.b[next].size()>0 && board.b[next].back().kind == 3){		//capstone found
			return false;
		}else if(board.b[next].size()>0 && board.b[next].back().kind == 2 && part/10 != 0){		//standing stone and moving stack beyond that
			return false;
		}else if(part/10 == 0 && board.b[next].size()>0 && board.b[next].back().kind == 2 && part > 1){		//standing stone and moving more than one size stack
			return false;
		}else if(part/10 == 0 && board.b[next].size()>0 && board.b[next].back().kind == 2 && part > 1){		//standing stone and moving more than one size stack
			return false;
		}else if(part/10 == 0 && board.b[next].size()>0 && board.b[next].back().kind == 2 && board.b[pos].back().kind != 3){		//standing stone and moving more than one size stack
			return false;
		}
		part/=10;
	}
	return true;
}

vector<string> generateStackMoves(Board& board,int pos){
	int r = pos / board.size, c = pos % board.size;
	int ups = r, downs = board.size - r - 1;
	int lefts = c, rights = board.size - c - 1;
	int stack_size = min( ((int) board.size) , (int)board.b[pos].size());
	// cerr<<"stack_size: "<<stack_size<<endl;

	char dir[4] = {'+','-','<','>'};			// + corresponds to right; - to left; < to up; > to down
	int rem_squares[4] = {0};
	rem_squares[0] = rights;
	rem_squares[1] = lefts;
	rem_squares[2] = ups;
	rem_squares[3] = downs;
	vector<int> part_list,part_dir;
	vector<string> all_moves;
	for(int i=1; i<= stack_size; i++){
		part_list.clear();
		part_list = partition(i);
		for (int j = 0; j < 4; ++j){
			part_dir.clear();
			for (int k = 0; k < part_list.size(); ++k){
				if(numDigits(part_list[k]) <= rem_squares[j]){
					part_dir.push_back(part_list[k]);
				}
			}
			for(int k=0;k<part_dir.size();k++){
				if( checkValid(board,pos,dir[j],part_dir[k])){
					all_moves.push_back(to_string(sumDigits(part_dir[k]))+ ((char) (r+'a')) + ((char) (c+'1')) + dir[j] + to_string(part_dir[k]));
				}
			}
		}
	}
	
	return all_moves;
}

vector<string> generateMoves(Board& board,int player){
	vector <string> all_moves;
	int pos=0;
	string tmp_move="";
	
	vector<string> stack_moves;
	if(board.p_flats[player] > 0 || board.p_caps[player] > 0){
		for (int i = 0; i < board.size; ++i){
			for (int j = 0; j < board.size; ++j){
				pos = i*board.size + j;
				
				//placing moves
				if(board.b[pos].size() == 0 ){	
					tmp_move = ((char) (i + 97)) + to_string(j+1);
					if(board.p_flats[player]>0){
						all_moves.push_back("F"+tmp_move);
						all_moves.push_back("S"+tmp_move);	
					}
					if(board.p_caps[player] > 0){
						all_moves.push_back("C"+tmp_move);
					}
				}
			}
		}
	}

	// cerr<<"yoyoyo"<<endl;
	for (int i = 0; i < board.size; ++i){
		for (int j = 0; j < board.size; ++j){
		//other moveStack moves			
			pos = i*board.size + j;
			if(board.b[pos].size()>0 && board.b[pos].back().color == board.player_color[player]){
				stack_moves = generateStackMoves(board,pos);
				if(stack_moves.size()>0){
					all_moves.insert(all_moves.end(), stack_moves.begin(),stack_moves.end());
				}
			}
		}
	}
	

	// cerr<<"All available moves are: ";
	// for (int i = 0; i < all_moves.size(); ++i){
	// 	cerr<<all_moves[i]<<", ";
	// }
	// cerr<<endl;
	return all_moves;
}

string minMove(int p,Board board, float& alpha, float& beta, int level){
	
	// cerr<<"Doing move at level: "<<level<<endl;
	if(level>=cut_off){
		float heuristic = evaluate(board);
		// cerr<<"Heuristic: "<<cost<<endl;
		// return cost;
		return to_string(heuristic)+"#";
	}

	float value = INT_MAX;

	// FOR ACTIONS
	string starred_move = "";
	string action_a_level_down="";
	string s = "";
	Board board_try = board; 

	float maxi = 0;
	vector<string> moves = generateMoves(board_try,p);
	string cmd;
	for (int i = 0; i < moves.size(); ++i){
		cmd = moves[i];
		board_try = board;
		if(cmd[0] == 'F' || cmd[0] == 'S' || cmd[0] == 'C'){
			placePiece(p,cmd,board_try);
		}else{
			moveStack(cmd,board_try);
		}

		s = maxMove(1-p,board_try,alpha,beta,level+1);
		maxi = 0;
		action_a_level_down = parseMove(s,maxi);

		if(maxi < value){			//findinf minimum
			value = maxi;
			starred_move = cmd;
		}
		if(alpha >= value){return (to_string(value)+"#"+starred_move);}
		beta = min(beta,value);
	}
	// return value;
	return (to_string(value)+"#"+starred_move);
}


string maxMove(int p,Board board, float& alpha, float& beta, int level){
	
	// cerr<<"Doing move at level: "<<level<<endl;
	if(level>=cut_off){
		float heuristic = evaluate(board);
		// cerr<<"Heuristic: "<<cost<<endl;
		return to_string(heuristic)+"#";
	}

	float value = INT_MIN;

	// FOR ACTIONS
	string starred_move = "";
	string action_a_level_down="";
	string s = "";

	Board board_try = board; 	

	float mini = 0;
	vector<string> moves = generateMoves(board_try,p);
	string cmd;
	for (int i = 0; i < moves.size(); ++i){
		cmd = moves[i];
		board_try = board;
		if(cmd[0] == 'F' || cmd[0] == 'S' || cmd[0] == 'C'){
			placePiece(p,cmd,board_try);
		}else{
			moveStack(cmd,board_try);
		}

		s = minMove(1-p,board_try,alpha,beta,level+1);
		mini = 0;
		action_a_level_down = parseMove(s,mini);

		if(mini > value){
			value = mini;
			starred_move = cmd;
		}
		if(value >= beta){return (to_string(value)+"#"+starred_move);}
		alpha = max(alpha,value);
	}

	return (to_string(value)+"#"+starred_move);
	// return value;
}

string doMove(Board& board,int level){

	string cmd;
	int player = 0;
	if(moves_me == 0){
		player = 1;
		if(board.b[0].size()==0){
			cmd="Fa1";
		}else{
			cmd="Fe1";
		}
		cerr<<"First opp piece placed: "<<cmd<<endl;
		placePiece(player,cmd,board);		
        board.printBoard();	
        moves_me ++;
        return cmd;
	}
	float alpha = INT_MIN;
	float beta = INT_MAX;
	
	
	string action;
	// player = 0;
	action = maxMove(player,board,alpha,beta,level);				// maxMove(player,board,alpha,beta,level)	

	// if(moves_me == 0){
	// 	player = 1;
	// 	action = minMove(player,board,alpha,beta,level);				// maxMove(player,board,alpha,beta,level)	

	// }else{
	// 	player = 0;
	// 	action = maxMove(player,board,alpha,beta,level);				// maxMove(player,board,alpha,beta,level)	
	// }


	float value = 0;
	// cerr<<"action: "<<action<<endl;
	action = parseMove(action,value);
	
	cmd = action;
	// if(moves_me==0){
	// 	cerr<<"First opp piece placed: "<<cmd<<endl;
	// }else{
		cerr<<"My Move: "<<cmd<<endl;	
	// }
	//actually doing move
	
	if(cmd[0] =='F' || cmd[0] =='S' || cmd[0] == 'C'){				//means that opponent has played a "place a stone"
        placePiece(player,cmd,board);		
        board.printBoard();	
    }else{									
   		moveStack(cmd,board);				
        board.printBoard();
    }
	
	// board.printBoard();
	
	moves_me ++;
	return action;
}

int main(){

	initialise();

	// Game Loop 
	string cmd;
	if(board.player_color[0] == 1){
		cmd = doMove(board,0);	
		cout<<cmd<<endl;	
	}


	while(true){
		if(finish_game){
			break;
		}

		// cin.ignore();
		cin >> cmd;
		int player = 0;
		if(moves_opp==0){
			player=0;
		}else{
			player=1;
		}
		cerr<<"Opponent's Move: "<<cmd<<endl;		
        if(cmd[0] =='F' || cmd[0]=='S' || cmd[0] =='C'){				//means that opponent has played a "place a stone"
            // cout << "row: "<<cmd[1]<<" | col: "<<cmd[2]<<" | pos: "<<pos<<endl;
            placePiece(player,cmd,board);		//1 -> opponent		// placepiece(player,cmd,board)
            board.printBoard();	
        }
        else{										//means that opp has played "move a stack", assuming the correct format of input here
       		moveStack(cmd,board);					// moveStack(cmd,board)
            board.printBoard();
        }
        moves_opp++;


        // cin.ignore();
		cmd = doMove(board,0);	
		cout<<cmd<<endl;	


	}

	return 0;
}