package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Bom;
import com.ledger.ledgerworks.entity.BomItem;
import com.ledger.ledgerworks.entity.ItemMaster;

import com.ledger.ledgerworks.repository.BomItemRepository;
import com.ledger.ledgerworks.repository.BomRepository;
import com.ledger.ledgerworks.repository.ItemMasterRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BomService {

    private final BomRepository bomRepository;

    private final BomItemRepository bomItemRepository;

    private final ItemMasterRepository itemRepo;

    public BomService(

            BomRepository bomRepository,

            BomItemRepository bomItemRepository,

            ItemMasterRepository itemRepo
    ) {

        this.bomRepository = bomRepository;

        this.bomItemRepository = bomItemRepository;

        this.itemRepo = itemRepo;
    }

    // =====================================================
    // SAVE BOM
    // =====================================================

    public Bom save(
            Bom request
    ) {

        Bom bom = new Bom();

        // =================================================
        // FINISHED GOOD FETCH
        // =================================================

        ItemMaster fg =
                itemRepo
                        .findById(
                                request
                                        .getFinishedGood()
                                        .getId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Finished Good not found"
                                )
                        );

        bom.setFinishedGood(fg);

        bom.setBomName(
                request.getBomName()
        );

        bom.setStatus(
                request.getStatus()
        );

        // =================================================
        // SAVE BOM FIRST
        // =================================================

        Bom savedBom =
                bomRepository.save(bom);

        // =================================================
        // SAVE ITEMS
        // =================================================

        List<BomItem> savedItems =
                new ArrayList<>();

        if (request.getItems() != null) {

            for (BomItem reqItem
                    : request.getItems()) {

                ItemMaster rm =
                        itemRepo
                                .findById(
                                        reqItem
                                                .getItem()
                                                .getId()
                                )
                                .orElseThrow(
                                        () -> new RuntimeException(
                                                "Raw Material not found"
                                        )
                                );

                BomItem item =
                        new BomItem();

                item.setBom(savedBom);

                item.setItem(rm);

                item.setQuantity(
                        reqItem.getQuantity()
                );

                item.setUnit(
                        reqItem.getUnit()
                );

                item.setRemarks(
                        reqItem.getRemarks()
                );

                savedItems.add(
                        bomItemRepository.save(item)
                );
            }
        }

        savedBom.setItems(savedItems);

        return savedBom;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<Bom> getAll() {

        return bomRepository.findAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    public Bom getById(
            Long id
    ) {

        return bomRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "BOM not found"
                        )
                );
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(
            Long id
    ) {

        bomRepository.deleteById(id);
    }
    public Bom getByFinishedGood(
            Long itemId
    ) {

        return bomRepository
                .findByFinishedGoodId(itemId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "BOM not found"
                        )
                );
    }
}