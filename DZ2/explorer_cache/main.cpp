#include <iostream>
#include <time.h>
#include <tgmath.h>
#include "create_list.h"

using namespace std;

#define THE_NUMBER_OF_REQUEST 200000
#define START_BLOCK_SIZE 8
#define FINISH_BLOCK_SIZE 1024*1024*16

#define NEXT1(x) x
#define NEXT16(x) x x x x x x x x x x x x x x x x
#define NEXT256(x) NEXT16(x) NEXT16(x) NEXT16(x) NEXT16(x)  NEXT16(x) NEXT16(x) NEXT16(x) NEXT16(x) NEXT16(x) NEXT16(x) NEXT16(x) NEXT16(x)  NEXT16(x) NEXT16(x) NEXT16(x) NEXT16(x)

int main() {
    element *list_elements = NULL;
    long int block_size = START_BLOCK_SIZE;

    while (block_size < FINISH_BLOCK_SIZE) {
        list_elements = create_random_list_elements(block_size);
        element *head = list_elements;

        for (long i = 0; i < block_size; ++i) {
            NEXT1(head = head->next;);
        }

        clock_t start_tact = clock();

        for (long i = 0; i < THE_NUMBER_OF_REQUEST; ++i) {
            NEXT256(head = head->next;);
        }

        clock_t time = (clock() - start_tact);

        printf("(%lf,%lf)\n", log2(block_size * sizeof(element)), ((double) time) * 1000 / (THE_NUMBER_OF_REQUEST * 256));
        free_list(list_elements, block_size);

        block_size *= 1.25;
    }

    return 0;
}




