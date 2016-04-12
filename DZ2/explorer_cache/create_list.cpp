//
// Created by ilyaps on 20.03.16.
//
#include <malloc.h>
#include <time.h>
#include <cstdlib>
#include "create_list.h"

element *create_list_elements(long size) {

    element *head = (element *)malloc(1 * sizeof(element));
    head->num = 0;
    element *next = head;
    for (long i = 1; i < size; ++i) {
        element *el = (element *)malloc(1 * sizeof(element));
        el->num = i;
        next->next = el;
        next = el;
    }
    next->next = head;

    return head;
}

element *create_random_list_elements(long size) {

    srand((unsigned) time(NULL));
    element *head = create_list_elements(size);

    element *el1 = head;
    element *el2 = head;

    long r1 = (rand() % size);
    for (int j = 0; j < r1; ++j) {
        el1 = el1->next;
    }

    for (int i = 1; i < size; ++i) {
        r1 = rand() % 20;
        if (i % 2) {
            for (int j = 0; j < r1; ++j) {
                el1 = el1->next;
            }
        }   else {
            for (int j = 0; j < r1; ++j) {
                el2 = el2->next;
            }
        }

        if (el1 == el2) {
            continue;
        }

        element *el3 = el2->next;

        element *next = el1->next;
        el1->next = el3;
        el2->next = el3->next;
        el3->next = next;
    }

    return head;
}

void free_list(element *head, int size) {
    element *next = head->next;
    for (int i = 0; i < size - 1; ++i) {
        free(head);
        head = next;
        next = head->next;
    }
}
