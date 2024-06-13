#include "kernel/types.h"
#include "user/user.h"

int main() {
  int pipe1[2];
  int pipe2[2];
  if (pipe(pipe1) == -1) {
    exit(0);
  }
  if (pipe(pipe2) == -1) {
    exit(0);
  }
  int pid;
  if ((pid = fork()) == -1) {
    exit(0);
  }
  if (pid == 0) {
    char buffer[128];
    while (read(pipe1[0], buffer, 128) == 0)
      ;
    printf("%d: got %s\n", getpid(), buffer);
    write(pipe2[1], "pong", 4);
  } else {
    write(pipe1[1], "ping", 4);
    char buffer[128];
    while (read(pipe2[0], buffer, 128) == 0)
      ;
    printf("%d: got %s\n", getpid(), buffer);
  }
  exit(0);
}
