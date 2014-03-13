; dados 2 vectores, copiar en un tercero las posiciones pars alternadas
; dejando en cant_elem la cantidad de elementos copiados


                        aseg
                        org 100h

inicio                  ld c, 0
                        ld a, c1-c2
                        cp 00h
                        jp nz, fin
                        ld ix, vector1 + 1
                        ld iy, vector2 + 1
                        ld hl, vector_resultado
                        ld b, c1
                        jp chequeo   ; caso 0 elementos
ciclo                   ld a, (ix)
                        ld (hl), a
                        ld a, (iy)
                        ld (hl), a
                        inc(ix)      ; avanzo en los vectores e incremento
                        inc(ix)      ; el contador de elementos copiados   
                        inc(iy)
                        inc(iy)
                        inc c
                        inc c
chequeo                 dec b
                        jp z, salida
                        djnz ciclo
salida                  ld a, c
                        ld (cant_elem), a
fin                     rst 38h

;datos

vector1                 db 1,2,3,4
c1                      equ $ - vector1
vector2                 db 1,2,3,4
c2                      equ $ - vector2
vector_resultado        ds c1*2
cant_elem               ds 1

                        end inicio
