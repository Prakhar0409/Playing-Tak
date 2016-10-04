#include <bits/stdc++.h>
#include "Board.h"
#define s(n) scanf("%d",&n)

using namespace std;

/* Global Vars - all underscore naming */
int cut_off = 5;

int board_size = 0;
int time_limit = 0;
bool finish_game = false;
Board board;

string maxMove(int p,Board board, float& alpha, float& beta, int level);
string minMove(int p,Board board, float& alpha, float& beta, int level);

void placePiece(int p, string cmd, Board& board){
	char first_char = cmd[0];
	int pos = (cmd[1] - 'a') * board_size + (cmd[2] - '0' - 1);			// pos => i*n + j-1
    if(first_char == 'C'){
   		board.p_caps[p]--;
   	}else{
   		board.p_flats[p]--;
   	}
   	BoardUnit bu = BoardUnit(board.player_num[p],(first_char=='F')?1:((first_char=='S')?2:3));
	board.addPiece(pos, bu);
}


void move(string cmd, Board& board){
	int pieces_to_move = 0;
	int i=0;
	while(cmd[i]>='0' && cmd[i] <='9'){
		pieces_to_move = pieces_to_move*10 + cmd[i]-'0';
		i++;
	}
	// cout << "Number of pieces to move: "<<pieces_to_move<<endl;
    int posn_alphabet = (int)(cmd[i++] - 'a');
    int posn_numeric = (int)(cmd[i++] - '0' - 1);
    int position = posn_alphabet * board_size + posn_numeric ;						//position of that piece on board
    char direction = (cmd[i++]);
   
   	if(board.b[position].size() < pieces_to_move){
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
    	next = board_size;
    	add_next = board_size;
    }else if(direction == '<'){
    	add_next = -board_size;
    	next = -board_size;
    }

    // cerr << "about to move:: "<<endl;
	int idx = board.b[position].size() - pieces_to_move;
	// cout<<"total: "<< board.b[position].size()<<"  ||  idx: "<<idx<<endl;
	for(int j=0;j<pieces_to_move;j++){
		// if((position+next)%board_size == board_size-1){
		// 	cout<<"Invalid move. Trying to move out of board"<<endl;
		// }
		int num = (int) (cmd[i] - '0');
		while(num--){
			board.b[position + next].push_back(board.b[position][idx++]);
			j++;
		}
		next += add_next;
		// cerr << "Pieces added to other side:: "<<endl;
		// board.printBoard();
	}

	idx = board.b[position].size() - pieces_to_move;
	board.b[position].resize(idx);
	// cerr << "Pieces Removed:: "<<endl;
}

void initialise(){
	// Intialisation phase
	int player_me;
	cin >> player_me;
	cin >> board_size;
	cin >> time_limit;
	board = Board(board_size,player_me);
	cerr << "Player: "<<board.player_num[0]<<" | board_size: "<<board_size<<" | time_limit: "<<time_limit<<endl;
}

float getOwnedSquaresDiff(Board& board){//returns number of squares owned by me and that by opponent in an array [p_me,p_opp]
    // int [] result = new int[2];
    float p_me=0;
    float p_opp = 0;
    int pos = 0;
    for(int i =0; i< board_size; i++){
        for(int j =0; j<board_size; j++){
        	pos = (i*board_size)+j;
        	if(board.b[pos].size() > 0 && board.b[pos].back().color == board.player_num[0]){
        		p_me++;
        	}else{
        		p_opp++;
        	}
        }
    }
    // result[0]=p_me;
    // result[1] = p_opp;
    // return result;
    return (p_me - p_opp);
}



float getStackHeightDiff(Board& board){
        // float result = new int[2];
        float p_me=0;
        float p_opp = 0;
        int pos = 0;
        for(int i = 0; i< board_size; i++){
            for(int j = 0; j< board_size; j++){
            	pos = (i*board_size)+j;
            	int stack_size = board.b[pos].size();
            	if( stack_size > 0 ){
            		if(board.b[pos].back().color == board.player_num[0]){						//"\n"[0] -> me
            			p_me += stack_size;
            		}else{
            			p_opp += stack_size;
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
    for(int i = 0; i< board_size; i++){
        for(int j = 0; j< board_size; j++){
            pos = (i*board_size)+j;
            int stack_size = board.b[pos].size();
            if(stack_size == 0 || j == 0){
            	cur_me = 0;
            	cur_opp = 0;
            }else{
            	if(board.b[pos].back().color == board.player_num[0]){
            		cur_me++;
            		cur_opp = 0;
            		if(cur_me > max_me){
            			max_me = cur_me;
            		}
            	}
            }
        }
    }

    for(int j = 0; j< board_size; j++){
        for(int i = 0; i< board_size; i++){
            pos = (i*board_size)+j;
            int stack_size = board.b[pos].size();
            if(stack_size == 0 || i == 0){
            	cur_me = 0;
            	cur_opp = 0;
            }else{
            	if(board.b[pos].back().color == board.player_num[0]){
            		cur_me++;
            		cur_opp = 0;
            		if(cur_me > max_me){
            			max_me = cur_me;
            		}
            	}
            }
        }
    }
    // result[0]=max_me;
    // result[1] = max_opp;
    // return result;
    return (max_me-max_opp);
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

float evaluate(Board& board){
	float result = (getStackHeightDiff(board));
	result += getOwnedSquaresDiff(board) ;
	result += (getMaxChainLengthDiff(board));
	// for (int i = 1; i < num_players; ++i){
		// result -= (this.getStackHeight(i)+this.getOwnedSquares(i)+this.getMaxChainLength(i));
	// }
    return result;
}

vector<string> generateMoves(Board& board,int player){
	vector <string> place_moves;
	int pos=0;
	string tmp_move="";
	if(board.p_flats[player] > 0 || board.p_caps[player] > 0){
		for (int i = 0; i < board_size; ++i){
			for (int j = 0; j < board_size; ++j){
				pos = i*board_size + j;
				if(board.b[pos].size() == 0 ){	
					tmp_move = ((char) (i + 97)) + to_string(j+1);
					
					if(board.p_flats[player]>0){
						place_moves.push_back("F"+tmp_move);
						place_moves.push_back("S"+tmp_move);	
					}
					if(board.p_caps[player] > 0){
						place_moves.push_back("C"+tmp_move);
					}
				}
			}
		}
	}
	return place_moves;
}

string minMove(int p,Board board, float& alpha, float& beta, int level){
	
	cerr<<"Doing move at level: "<<level<<endl;
	if(level>=cut_off){
		float cost = evaluate(board);
		cerr<<"Heuristic: "<<cost<<endl;
		// return cost;
		return to_string(cost)+"#";
	}

	float value = INT_MAX;

	// FOR ACTIONS
	string starred_move = "";
	string action_a_level_down="";
	string s = "";
	Board board_try = board; 
	int pos = 0;
	cerr<<"PLACING MOVES:"<<endl;
	vector <string> place_moves;
	string tmp_move = "";
	if(board.p_flats[1] > 0 || board.p_caps[1] > 0){
		cerr<<"Options of placing Flat or Standing stones: "<<endl;
		for (int i = 0; i < board_size; ++i){
			for (int j = 0; j < board_size; ++j){
				pos = i*board_size + j;
				float maxi = 0;
				if(board.b[pos].size() == 0 ){	
					tmp_move = ((char) (i + 97)) + to_string(j+1);
					if(board.p_flats[1]>0){
						board_try = board;
						cerr<<"move: "<<"PLACE PIECE at: ("<< (char)(i+97) <<","<<j+1<<")"<<endl;
	
						placePiece(p,"F"+tmp_move,board_try);

						//////////// max move and parsing max move action
						s = maxMove(1-p,board_try,alpha,beta,level+1);
						maxi = 0;
						action_a_level_down = parseMove(s,maxi);

						
						if(maxi < value){			//findinf minimum
							value = maxi;
							starred_move = "F"+tmp_move;		//this is my min move
						}
						if(alpha >= value){return (to_string(value)+"#"+starred_move);}
						beta = min(beta,value);

						board_try = board;
						placePiece(p,"S"+tmp_move,board_try);
						// doMove(1-p,board_try,level+1);
						s = maxMove(1-p,board_try,alpha,beta,level+1);
						maxi = 0;
						action_a_level_down = parseMove(s,maxi);

						if(maxi > value){
							value = maxi;
							starred_move = "S"+tmp_move;
						}
						if(alpha >= value){return (to_string(value)+"#"+starred_move);}
						// if(alpha >= value){return value;}
						beta = min(beta,value);


						cerr << "F"<<tmp_move<<", S"<<tmp_move<<", ";
						place_moves.push_back("F"+tmp_move);
						place_moves.push_back("S"+tmp_move);	
					}
					if(board.p_caps[p] > 0){
						board_try = board;
						placePiece(p,"C"+tmp_move,board_try);
						s = maxMove(1-p,board_try,alpha,beta,level+1);
						maxi = 0;
						action_a_level_down = parseMove(s,maxi);
						if(maxi > value){
							value = maxi;
							starred_move = "C"+tmp_move;
						}
						if(alpha >= value){return (to_string(value)+"#"+starred_move);}
						// if(alpha >= value){return value;}
						beta = min(beta,value);

						cerr << "C"<<tmp_move<<", ";
						place_moves.push_back("C"+tmp_move);
					}
				}
			}
		}
		cerr<<endl;
	}

	// return value;
	return (to_string(value)+"#"+starred_move);
}


string maxMove(int p,Board board, float& alpha, float& beta, int level){
	
	cerr<<"Doing move at level: "<<level<<endl;
	if(level>=cut_off){
		float cost = evaluate(board);
		cerr<<"Heuristic: "<<cost<<endl;
		return to_string(cost)+"#";
	}

	float value = INT_MIN;

	// FOR ACTIONS
	string starred_move = "";
	string action_a_level_down="";
	string s = "";


	Board board_try = board; 
	

	int pos = 0;
	cerr<<"PLACING MOVES:/"<<endl;
	vector <string> place_moves;
	string tmp_move = "";
	if(board.p_flats[0] > 0 || board.p_caps[0] > 0){
		cerr<<"Options of placing Flat or Standing stones: "<<endl;
		for (int i = 0; i < board_size; ++i){
			for (int j = 0; j < board_size; ++j){
				pos = i*board_size + j;
				float mini = 0;
				if(board.b[pos].size() == 0 ){	
					tmp_move = ((char) (i + 97)) + to_string(j+1);
					if(board.p_flats[0]>0){
						board_try = board;
						cerr<<"move: "<<"PLACE PIECE at: ("<< (char)(i+97) <<","<<j+1<<")"<<endl;
						placePiece(p,"F"+tmp_move,board_try);

						// mini = minMove(1-p,board_try,alpha,beta,level+1);
						s = minMove(1-p,board_try,alpha,beta,level+1);
						mini = 0;
						action_a_level_down = parseMove(s,mini);

						if(mini > value){
							value = mini;
							starred_move = "F"+tmp_move;
						}
						if(value >= beta){return (to_string(value)+"#"+starred_move);}
						alpha = max(alpha,value);


						board_try = board;
						placePiece(p,"S"+tmp_move,board_try);
						// doMove(1-p,board_try,level+1);
						// mini = minMove(1-p,board_try,alpha,beta,level+1);
						s = minMove(1-p,board_try,alpha,beta,level+1);
						mini = 0;
						action_a_level_down = parseMove(s,mini);
						
						if(mini > value){
							value = mini;
							starred_move = "S"+tmp_move;
						}
						if(value >= beta){return (to_string(value)+"#"+starred_move);}
						alpha = max(alpha,value);


						cerr << "F"<<tmp_move<<", S"<<tmp_move<<", ";
						place_moves.push_back("F"+tmp_move);
						place_moves.push_back("S"+tmp_move);	
					}
					if(board.p_caps[p] > 0){
						board_try = board;
						placePiece(p,"C"+tmp_move,board_try);
						// mini = minMove(1-p,board_try,alpha,beta,level+1);
						s = minMove(1-p,board_try,alpha,beta,level+1);
						mini = 0;
						action_a_level_down = parseMove(s,mini);
						
						if(mini > value){
							value = mini;
							starred_move = "C"+tmp_move;
						}
						// if(value >= beta){return value;}
						if(value >= beta){return (to_string(value)+"#"+starred_move);}
						alpha = max(alpha,value);

						cerr << "C"<<tmp_move<<", ";
						place_moves.push_back("C"+tmp_move);
					}
				}
			}
		}
		cerr<<endl;
	}
	
	return (to_string(value)+"#"+starred_move);
	// return value;
}

string doMove(Board board,int level){

	// cout <<" hey"<<endl;
	float alpha = INT_MIN;
	float beta = INT_MAX;
	board.printBoard();
	string action = maxMove(0,board,alpha,beta,0);				// maxMove(player,board,alpha,beta,)
	float value = 0;
	cout<<"action: "<<action<<endl;
	action = parseMove(action,value);
	cout <<"DNJABKHFB0"<<endl;
	board.printBoard();

	// Board board_try = board;
	


	// cerr<<"MOVING MOVES:"<<endl;
	// for (int i = 0; i < board_size; ++i){
	// 	for (int j = 0; j < board_size; ++j){
	// 		pos = i*board_size + j;
	// 		if(board.b[pos].size() == 0 ){
	// 			tmp_move = ((char) (i + 97)) + to_string(j+1);
	// 			// cerr<<"tmp: "<<tmp_move<<endl;
	// 			cerr << "F"<<tmp_move<<", S"<<tmp_move<<", ";
	// 			place_moves.push_back("F"+tmp_move);
	// 			place_moves.push_back("S"+tmp_move);
	// 			if(board.p_caps[p] > 0){
	// 				cerr << "C"<<tmp_move<<", ";
	// 				place_moves.push_back("C"+tmp_move);
	// 			}
	// 		}
	// 	}
	// }
	// cerr<<endl;

	// placePiece();

	return action;
}

int main(){

	initialise();


	// Game Loop 
	string cmd;
	if(board.player_num[0] == 1){
		cmd = doMove(board,0);	
		cout<<"My Move: "<<doMove<<endl;	
		char first_char = cmd[0];
		if(first_char=='F' || first_char=='S' || first_char=='C'){				//means that opponent has played a "place a stone"
            placePiece(0,cmd,board);		//1 -> opponent		// placepiece(player,cmd,board)
            board.printBoard();	
        }else{										//means that opp has played "move a stack", assuming the correct format of input here
       		move(cmd,board);				// move(cmd,board)
            board.printBoard();
        }
	}


	while(true){
		if(finish_game){
			break;
		}

		cin >> cmd;			//move that opponent played
        char first_char = cmd[0];
        if(first_char=='F' || first_char=='S' || first_char=='C'){				//means that opponent has played a "place a stone"
            
            // cout << "row: "<<cmd[1]<<" | col: "<<cmd[2]<<" | pos: "<<pos<<endl;
            placePiece(1,cmd,board);		//1 -> opponent		// placepiece(player,cmd,board)
            board.printBoard();	
        }
        else{										//means that opp has played "move a stack", assuming the correct format of input here
       		move(cmd,board);				// move(cmd,board)
            board.printBoard();
        }

        
		//mera move
		cmd = doMove(board,0);		
		cout<<"My Move: "<<doMove<<endl;	
		if(first_char=='F' || first_char=='S' || first_char=='C'){				//means that opponent has played a "place a stone"
            placePiece(0,cmd,board);		//1 -> opponent		// placepiece(player,cmd,board)
            board.printBoard();	
        }else{										//means that opp has played "move a stack", assuming the correct format of input here
       		move(cmd,board);				// move(cmd,board)
            board.printBoard();
        }

	}


	return 0;
}