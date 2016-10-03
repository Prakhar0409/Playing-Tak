#include <bits/stdc++.h>
#include "Board.h"

using namespace std;

Board::Board(){

}

Board::Board(int bsize){
	size = bsize;	
    for (int i = 0; i < num_players; ++i)
    {
        /* code */
    }
    num_players = 2;
    player_num[2] = {0};        // p[0] -> us; p[1] => opponent
    p_flats[2] = {0};
    p_caps[2] = {0};
}

void Board::addPiece(int pos, BoardUnit bu){
	b[pos].push_back(bu);
}

void Board::printBoard(){
    int board_size = size*size;
    for(int i =0; i<size; i++){
        string res = "";
        cout << ((char) (i + 97));
        // cout<<"RES: "<<res<<"  ||  c: "<<(char)(i+97)<<endl;
        for(int j = 0; j < size; j++){
            int pos = i*size+j;
            string entry_set = "[";
            for(int k=0; k<b[pos].size(); k++){
                entry_set = entry_set + "("+ to_string(b[pos][k].color) + ","+ to_string(b[pos][k].kind)+"),";
            }
            entry_set = entry_set + "]";
            res = res + to_string(j+1)+" : "+ entry_set+"; ";
        }
        cout<< res << endl;
    }
}

Board::~Board(){

}