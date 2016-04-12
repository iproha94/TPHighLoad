//
// Created by ilyaps on 20.03.16.
//

#ifndef EXPLORER_CACHE_CREATE_LIST_H
#define EXPLORER_CACHE_CREATE_LIST_H

#define SIZE_ARRAY_IN_ELEMENT 1

struct element {
    long array[SIZE_ARRAY_IN_ELEMENT];
    element *next;
    long num;
};


element *create_list_elements(long size);
element *create_random_list_elements(long size);
void free_list(element *head, int size);

#endif //EXPLORER_CACHE_CREATE_LIST_H
