; dados 2 vectores poner en la posicion i de un tercer vector,
; 1 si los elementos i de cada vector son iguales y 0 si no lo son
; en caso de teren los vctores distintas dimensiones salir del programa.


                        aseg
                        org 100h

inicio                  ld a, cardinalidad1 - cardinalidad2
                        cp 0
                        jp nz, fin  ; chequeo dimensiones!

                        ld ix, vector1
                        ld iy, vector2
                        ld hl, vector3
                        ld b, cardinalidad1 + 1
                        jp chequeo ; caso vector vacio
ciclo                   ld a, (ix)
                        ;TODO sub a, (iy)
                        sub (iy)
                        jp z, sigo
                        ld a, 1
sigo                    neg
                        add a, 1
                        ;add 1
                        ld (hl), a
                        inc ix
                        inc iy
                        inc hl
chequeo                 djnz ciclo
fin                     rst 38h


;datos
vector1                 db 1,2,3
cardinalidad1           equ $-vector1
vector2                 db 1,4,3
cardinalidad2           equ $-vector2
vector3                 ds cardinalidad1
                        end inicio
