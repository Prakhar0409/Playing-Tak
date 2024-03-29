#include <bits/stdc++.h>
#include "BoardUnit.h"
#define N 65

using namespace std;

class Board
{
public:
	int size;
	vector < BoardUnit > b[N];
	int num_players;// = 2;
	int player_color[2]; //= {0};		// p[0] -> us; p[1] => opponent
	int color_to_player[2]; //= {0};		
	int p_flats[2]; //= {0};
	int p_caps[2]; 	//= {0};
	int init_flats;
	bool debug;
	int area;

public:
	Board();
	Board(int bsize,int player_me);
	void addPiece(int pos, BoardUnit bu);
	void printBoard();
	void forcePrintBoard();
	~Board();
	/* data */
};
