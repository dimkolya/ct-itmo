composite_table(1).

init(MAX_N) :- iterate(2, MAX_N).

iterate(I, MAX_N) :-
    I_SQUARE is I * I,
    I_SQUARE > MAX_N,
    !.
iterate(I, MAX_N) :-
    prime(I),
    I_SQUARE is I * I,
    fill_table(I_SQUARE, I, MAX_N).
iterate(I, MAX_N) :-
    I1 is I + 1,
    iterate(I1, MAX_N).

fill_table(J, I, MAX_N) :-
    J =< MAX_N,
    assert(composite_table(J)),
    JI is J + I,
    fill_table(JI, I, MAX_N).

composite(N) :- composite_table(N).
prime(N) :- number(N), \+ composite_table(N).

next_prime_divisor(N, P, P) :- 0 is N mod P, !.
next_prime_divisor(N, H, P) :-
    P1 is P + 1,
    next_prime_divisor(N, H, P1).

prime_divisors(1, []) :- !.
prime_divisors(N, [N]) :- prime(N), !.
prime_divisors(N, Divisors) :-
    number(N), !,
    prime_divisors_(N, Divisors, 2).
prime_divisors(N, [H1, H2 | T]) :-
    number(H1), number(H2), !,
    H1 =< H2,
    prime(H1),
    prime_divisors(NH, [H2 | T]),
    N is NH * H1.

prime_divisors_(N, [N], _) :- prime(N), !.
prime_divisors_(N, [H | T], P) :-
    next_prime_divisor(N, H, P),
    NH is N / H,
    prime_divisors_(NH, T, H).

union(Divisors, [], Divisors) :- !.
union([], Divisors, Divisors) :- !.
union([H | T1], [H | T2], [H | T]) :- !, union(T1, T2, T).
union([H1 | T1], [H2 | T2], [H1 | T]) :- H1 =< H2, union(T1, [H2 | T2], T).
union([H1 | T1], [H2 | T2], [H2 | T]) :- H1 > H2, union([H1 | T1], T2, T).

lcm(A, B, LCM) :-
    prime_divisors(A, DivisorsA),
    prime_divisors(B, DivisorsB),
    union(DivisorsA, DivisorsB, Divisors),
    prime_divisors(LCM, Divisors).
