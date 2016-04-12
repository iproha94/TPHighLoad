#include <iostream>
#include "create_list.h"

using namespace std;

#define BLOCK_SIZE 10


int main2() {

    element *list_elements = NULL;
    list_elements = create_random_list_elements(BLOCK_SIZE);

    for(int i = 0; i < BLOCK_SIZE; ++i) {
        printf("%ld %d \n", list_elements->num , &(list_elements->next->next) - &(list_elements->next));
        list_elements = list_elements->next;
    }

    free_list(list_elements, BLOCK_SIZE);

    return 0;
}