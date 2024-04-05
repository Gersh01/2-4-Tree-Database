#include <iostream>
#include <cstdlib>
#include <fstream>

#define NUMADD 3000
#define NUMMAX 100

using namespace std;

int main() {
    
    ofstream MyFile("insertDelete1.txt");

    for (int i = 1; i <= NUMADD; i++) {
        MyFile << 1 << " ";
        MyFile << i << "\n";

    }
    for (int i = 1; i <= NUMADD; i++) {
        MyFile << 2 << " ";
        MyFile << i << "\n";

    }
    
    MyFile << 4 << "\n";
    MyFile << 5;

    MyFile.close();

    return 0;
}
