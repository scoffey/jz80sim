;Biblioteca para el tp 7


;****************************************************************************
;interface

                        Global Intercambiar
;subrutina que intercambia los contedidos de ix e iy

;----------------------------------------------------------------------------
                        Global Fibonacci
;subrutina que devuelve los primeros n elementos de la sucesion de Fibonacci.
;los par metros se mandan por stack en el siguiente orden de arriba hacia abajo:
;n, direcci¢n
;----------------------------------------------------------------------------




;implementation

                        cseg
Intercambiar
                        push af
                        push bc

                        ld a, (ix)
                        ld b, a
                        ld a, (iy)
                        ld (ix), a
                        ld a, b
                        ld (iy), a

                        pop bc
                        pop af
                        ret


                        cseg
Fibonacci
                        ld (Fib_backup), ix         ;backupïs
                        ld (Fib_backup+2), iy
                        ld (Fib_backup+4), hl
                        ld (Fib_backup+6), bc

                        pop iy
                        pop hl          ;h=n
                        pop ix          ;direccion
                        push iy         ;guardo ret en la pila
                        push af
                        ld a, h
                        inc a           ;a=n+1
                        ld l, h         ;h=n
                        dec l           ;l = n-1
                        jp Fib_check    ;0 elementos?

Fib_ciclo               cp l           ;controlo Fib_0
                        jp NZ, Fib_sigo
                        ld (ix), 1
                        jp Fib_continuo

Fib_sigo                cp h           ;controlo Fib_1
                        jp NZ, Fib_sigo2
                        ld (ix), 1
                        jp Fib_continuo

Fib_sigo2               ld b, a        ;Fib_m
                        ld a, (ix-2)
                        add a, (ix-1)
                        ld (ix), a
                        ld a, b
Fib_continuo            inc ix
Fib_check               dec a
                        jp NZ, Fib_ciclo

                        pop af
                        ld ix, (Fib_backup)
                        ld iy, (Fib_backup+2)
                        ld hl, (Fib_backup+4)
                        ld bc, (Fib_backup+6)
                        ret

                        dseg
Fib_backup              ds 8


                        cseg

sacar_repeticiones 	macro direccion, cant, reg
;TODOmacro                   sacar_repeticiones direccion, cant, reg
                        local Srep_ciclo, Srep_sigo, Srep_check, Srep_backup
                        push af                 ;backupïs
                        ld (Srep_backup), ix
                        ld (Srep_backup+2), iy
                        ld (Srep_backup+4), bc
                                         
                        ld b, cant
                        inc b
                        ld ix, direccion+1
                        ld iy, direccion
                        jp Srep_check            ;0 elementos?
Srep_ciclo              ld a, (iy)
                        cp (ix)
                        jp NZ, Srep_sigo
                        inc c
                        ld a, (ix)
                        ld (iy), a
Srep_sigo               inc ix
                        inc iy
Srep_check              djnz Srep_ciclo
                        ld reg, c

                        pop af                  ;restauro backupïs
                        ld ix, (Srep_backup)
                        ld iy, (Srep_backup+2)
                        ld bc, (Srep_backup+4)
                        jr Srep_backup+6
Srep_backup             ds 6
                        endm


