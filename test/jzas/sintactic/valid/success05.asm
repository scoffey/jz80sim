                aseg
                org 100h
inicio          ld ix, vector_fuente
                ld iy, vector_destino
                ld de, 0
                ld b, cant_elem + 1
                jp control1
ciclo1          ld a, (ix)
                ld (iy), a
                inc de
                inc iy
                inc ix
                inc ix
                dec b
                jp z, salida1
control1        djnz ciclo1
salida1         ld ix, vector_fuente +1
                ld b, cant_elem
                ld iy, vector_destino + 1
                add iy, de
                jp control2
ciclo2          ld a, (ix)
                ld (iy), a
                inc ix
                inc ix
                inc iy
                dec b
                jp z, salida2
control2        djnz ciclo2
salida2         rst 38h


vector_fuente   db 1, 2, 3, 4, 5, 6
cant_elem       equ $ - vector_fuente
vector_destino  ds cant_elem
                end inicio
              
