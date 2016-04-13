#include <iostream>
#include <syslog.h>

#include <stdio.h>

#include <sys/resource.h>
#include <sys/time.h>
#include <unistd.h>
using namespace std;

struct cpu{
    long  us, ni, sy, id;
} ;

struct cpu *get_cpu_c(FILE *stat) {
    struct cpu *cpu_c = (cpu *) malloc(sizeof(struct cpu));
    fscanf(stat, "cpu %lu %lu %lu %lu", &(cpu_c->us), &(cpu_c->ni), &(cpu_c->sy), &(cpu_c->id));

    return cpu_c;
}

double nonwork() {
    FILE *stat = fopen("/proc/stat", "r");
    if(!stat) {
        syslog(LOG_ERR, "Stat open failed!");
        exit(0);
    }

    cpu *cpu0 = get_cpu_c(stat);
    sleep(2);
    cpu *cpu1 = get_cpu_c(stat);

    fclose(stat);

    long  total0 = cpu0->id + cpu0->ni + cpu0->sy + cpu0->us;
    long  total1 = cpu1->id + cpu1->ni + cpu1->sy + cpu1->us;


    double info = (double)(cpu1->id - cpu0->id) / (total1 - total0);

    return info;
}

int main() {
    cout << nonwork();

    return 0;
}