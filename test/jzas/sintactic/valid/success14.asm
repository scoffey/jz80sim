                aseg
                org 100h
inicio          ld a, (num)
                cp 0
                inc a
                cp a
                ;cp 
                jp z, cero
                ld b, a
                ld c, 1
                ld a, 2

                
ciclo           cp b
                jp p, fin_ciclo
                add a, a
                inc c
                jp ciclo
fin_ciclo       jr nz, no_cero
                ld a, c
                jr fin
no_cero         ld a, c
                dec a
                jr fin
cero            ld a, 1
fin             ld (rta), a
                rst 38h

num             db 17
rta             ds 1
                end inicio
