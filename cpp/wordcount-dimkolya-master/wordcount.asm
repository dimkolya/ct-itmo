sys_exit:       equ             60

                section         .text
                global          _start

buf_size:       equ             8192
_start:
                xor             rbx, rbx
                sub             rsp, buf_size
                mov             rsi, rsp
                mov	r8b, 1
                xor	r9, r9

read_again:
                xor             rax, rax
                xor             rdi, rdi
                mov             rdx, buf_size
                syscall

                test            rax, rax
                jz              quit
                js              read_error

                xor             rcx, rcx

check_char:
                cmp             rcx, rax
                je              read_again
                xor	r10, r10
                mov	r10b, byte [rsi + rcx]
                lea	r11, [r10 - 9]
                cmp             r11b, 4
                setbe	r12b
                cmp	r10b, 32
                sete	r11b
                or	r11b, r12b
                je	add_word
                mov	r8b, r11b
                jmp             skip
                
add_word:
	test	r8b, r8b
	je	skip
	xor	r8b, r8b
                inc             rbx
                
skip:
                inc             rcx
                jmp             check_char

quit:
                mov             rax, rbx
                call            print_int

                mov             rax, sys_exit
                xor             rdi, rdi
                syscall

; rax -- number to print
print_int:
                mov             rsi, rsp
                mov             rbx, 10

                dec             rsi
                mov             byte [rsi], 0x0a

next_char:
                xor             rdx, rdx
                div             rbx
                add             dl, '0'
                dec             rsi
                mov             [rsi], dl
                test            rax, rax
                jnz             next_char

                mov             rax, 1
                mov             rdi, 1
                mov             rdx, rsp
                sub             rdx, rsi
                syscall

                ret

read_error:
                mov             rax, 1
                mov             rdi, 2
                mov             rsi, read_error_msg
                mov             rdx, read_error_len
                syscall

                mov             rax, sys_exit
                mov             rdi, 1
                syscall

                section         .rodata

read_error_msg: db              "read failure", 0x0a
read_error_len: equ             $ - read_error_msg
