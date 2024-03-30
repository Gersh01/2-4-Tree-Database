#include <iostream>
#include <cstdlib>
#include <fstream>

#define NUMADD 30000
#define NUMMAX 1000

using namespace std;

int main() {
    
    ofstream MyFile("insertions7.txt");

    for (int i = 0; i < NUMADD; i++) {
        MyFile << 1 << " ";
        MyFile << (rand() % NUMMAX) << "\n";
    }
    MyFile << 4 << "\n";
    MyFile << 5;

    MyFile.close();

    return 0;
}
